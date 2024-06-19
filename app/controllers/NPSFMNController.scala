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

package controllers

import auth.FMNAuth

import javax.inject.{Inject, Singleton}
import models.CorrelationId
import models.nps.NPSFMNRequest
import play.api.{Configuration, Environment, Logging}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{Action, MessagesControllerComponents, Results}
import services.NPSFMNService
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import scala.concurrent.ExecutionContext

@Singleton()
class NPSFMNController @Inject()(
                                  override val messagesApi: MessagesApi,
                                  val controllerComponents: MessagesControllerComponents,
                                  val authConnector: AuthConnector,
                                  npsFMNService: NPSFMNService
                                )(implicit val config: Configuration,
                                  val env: Environment,
                                  ec: ExecutionContext
                                ) extends BackendBaseController with FMNAuth with AuthorisedFunctions with I18nSupport with Logging {

  implicit val format: OFormat[NPSFMNRequest] = Json.format[NPSFMNRequest]

  def sendLetter(nino: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    authorisedAsFMNUser { _ => {

      val passRequest = request.body.as[NPSFMNRequest]
      implicit val correlationId: CorrelationId = CorrelationId.random
      for {
        httpResponse <- npsFMNService.sendLetter(nino, passRequest)
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
