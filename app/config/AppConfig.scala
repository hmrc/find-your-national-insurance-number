/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  lazy val hipProtocol: String = config.get[String]("microservice.services.nps-fmn-api.protocol")
  lazy val hipHost: String = config.get[String]("microservice.services.nps-fmn-api.host")
  lazy val hipPort: String = config.get[String]("microservice.services.nps-fmn-api.port")

  val npsFMNAPIUrl: String = s"$hipProtocol://$hipHost:$hipPort"


  lazy val individualDetailsProtocol: String = config.get[String]("external-url.individual-details.protocol")
  lazy val individualDetailsHost: String = config.get[String]("external-url.individual-details.host")
  lazy val individualDetailsPort: String = config.get[String]("external-url.individual-details.port")
  val individualDetailsServiceUrl: String = s"$individualDetailsProtocol://$individualDetailsHost:$individualDetailsPort"

  def individualDetails: DesApiServiceConfig =
    DesApiServiceConfig(config.get[Configuration]("microservice.services.individual-details"))

}
