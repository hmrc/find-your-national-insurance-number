/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  lazy val hipProtocol: String = config.get[String]("microservice.services.nps-fmn-api.protocol")
  lazy val hipHost: String = config.get[String]("microservice.services.nps-fmn-api.host")
  lazy val hipPort: String = config.get[String]("microservice.services.nps-fmn-api.port")

  val npsFMNAPIUrl: String = s"$hipProtocol://$hipHost:$hipPort"

}
