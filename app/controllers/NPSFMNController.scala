/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package controllers

import auth.FMNAuth

import javax.inject.{Inject, Singleton}
import config.AppConfig
import models.nps.NPSFMNRequest
import play.api.{Configuration, Environment, Logging}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsValue, Json, OFormat, OWrites}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result, Results}
import services.NPSFMNService
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class NPSFMNController @Inject()(
                                  override val messagesApi: MessagesApi,
                                  val controllerComponents: MessagesControllerComponents,
                                  val authConnector: AuthConnector,
                                  npsFMNService: NPSFMNService
                                )(implicit val config: Configuration,
                                  val env: Environment,
                                  ec: ExecutionContext,
                                  appConfig: AppConfig
                                ) extends BackendBaseController with FMNAuth with AuthorisedFunctions with I18nSupport with Logging {

  implicit val format: OFormat[NPSFMNRequest] = Json.format[NPSFMNRequest]
  implicit val formatHttpResponse: OWrites[HttpResponse] = Json.writes[HttpResponse]

  def onPageLoad(nino: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    authorisedAsFMNUser { authContext => {

      val passRequest = request.body.as[NPSFMNRequest]

      for {
        httpResponse <- npsFMNService.updateDetails(nino, passRequest)
      } yield httpResponse.status match {
        case 202 => Results.Accepted(httpResponse.body)
        case 400 => Results.BadRequest(httpResponse.body)
        case 401 => Results.Unauthorized(httpResponse.body)
        case 404 => Results.NotFound(httpResponse.body)
        case 500 => Results.InternalServerError(httpResponse.body)
        case 501 => Results.NotImplemented(httpResponse.body)
        // Add more cases as needed for other status codes
        case _ => Results.Status(httpResponse.status)(httpResponse.body)
      }
    }
    }
  }

}
