/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class MicroserviceHelloWorldController @Inject()(cc: ControllerComponents)
    extends BackendController(cc) {

  def hello(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Hello world"))
  }
}