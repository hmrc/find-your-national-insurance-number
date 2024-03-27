/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package controllers

import auth.FMNAuth
import connectors.IndividualDetailsConnector
import models.CorrelationId
import play.api.{Configuration, Environment, Logging}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Results}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class IndividualsDetailsController  @Inject()(
                                               override val messagesApi: MessagesApi,
                                               val controllerComponents: MessagesControllerComponents,
                                               val authConnector: AuthConnector,
                                                individualDetailsConnector: IndividualDetailsConnector
                                             )(implicit val config: Configuration,
                                               val env: Environment,
                                               ec: ExecutionContext,
                                             ) extends BackendBaseController with FMNAuth with I18nSupport with Logging {

  def getIndividualDetails(nino: String, resolveMerge: String): Action[AnyContent] = Action.async { implicit request =>
    authorisedAsFMNUser { _ => {
      implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
      for {
        httpResponse <- individualDetailsConnector.getIndividualDetails(nino, resolveMerge)
      } yield httpResponse.status match {
        case 200 => Results.Ok(httpResponse.body)
        case 400 => Results.BadRequest(httpResponse.body)
        case 401 => Results.Unauthorized(httpResponse.body)
        case 404 => Results.NotFound(httpResponse.body)
        case 500 => Results.InternalServerError(httpResponse.body)
        case 501 => Results.NotImplemented(httpResponse.body)
        // Add more cases as needed for other status codes
        case _ => Results.Status(httpResponse.status)(httpResponse.body)
      }

    }}
  }

}
