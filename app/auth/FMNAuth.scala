/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package auth

import play.api.Logging
import play.api.mvc.*
import play.api.mvc.Results.Unauthorized
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{credentialRole, internalId}
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthProviders, AuthorisationException, AuthorisedFunctions, CredentialRole, User}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

final case class AuthContext[A](
                                 isUser:     Boolean,
                                 internalId: String,
                                 request:    Request[A]
                               )

trait FMNAuth extends AuthorisedFunctions with Logging{
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
          Future successful Unauthorized
      }
      .recover {
        case ex: AuthorisationException =>
          Unauthorized
        case ex =>
          Unauthorized
      }
  }
}
