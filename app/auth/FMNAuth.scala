/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package auth

import play.api.Logging
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{credentialRole, internalId, nino}
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthProviders, AuthorisationException, AuthorisedFunctions, CredentialRole, User}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

final case class AuthContext[A](
                                 isUser:     Boolean,
                                 internalId: String,
                                 request:    Request[A]
                               )

trait FMNAuth extends AuthorisedFunctions with AuthRedirects with Logging{
  private type FMNAction[A] = AuthContext[A] => Future[Result]
  private val AuthPredicate = AuthProviders(GovernmentGateway)
  val FMNRetrievals: Retrieval[Option[CredentialRole] ~ Option[String]] = credentialRole and internalId

  def authorisedAsFMNUser(body: FMNAction[Any])
  (implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[_]): Future[Result] = authorisedUser(body)

  // $COVERAGE-OFF$
  def authorisedAsFMNUser(implicit
                          ec: ExecutionContext,
                          cc: ControllerComponents
                         ): ActionBuilder[AuthContext, AnyContent] =
    new ActionBuilder[AuthContext, AnyContent] {
      override protected def executionContext: ExecutionContext = ec
      override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

      override def invokeBlock[A](request: Request[A], authContext: AuthContext[A] => Future[Result]): Future[Result] = {
        implicit val req: Request[A] = request
        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

        authorisedUser(authContext)
      }
    }
  // $COVERAGE-ON$

  private def authorisedUser[A](
                                 block: FMNAction[A]
                               )(implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[A]): Future[Result] = {
    authorised(AuthPredicate)
      .retrieve(FMNRetrievals) {
        case Some(User) ~ Some(internalId) =>
          block(AuthContext(isUser = true, internalId, request))
        case _ =>
          logger.warn("user was not authenticated with required credentials")
          Future successful Unauthorized
      }
      .recover {
        case ex: AuthorisationException =>
          logger.warn("could not authenticate user.")
          logger.debug("could not authenticate user.", ex)
          Unauthorized
        case ex =>
          logger.warn("user was not authenticated.")
          logger.debug("user was not authenticated.", ex)
          Unauthorized
      }
  }
}
