/*
 * Copyright 2023 HM Revenue & Customs
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
