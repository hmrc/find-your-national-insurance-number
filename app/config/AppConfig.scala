/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  val appName: String = config.get[String]("appName")

  lazy val npsFMNAPICorrelationIdKey: String = config.get[String]("microservice.services.nps-fmn-api.correlationId.key")
  lazy val npsFMNAPIOriginatorIdKey: String = config.get[String]("microservice.services.nps-fmn-api.govUkOriginatorId.key")
  lazy val npsFMNAPIOriginatorIdValue: String = config.get[String]("microservice.services.nps-fmn-api.govUkOriginatorId.value")

  lazy val npsFMNAPIProtocol: String = config.get[String]("microservice.services.nps-fmn-api.protocol")
  lazy val npsFMNAPIHost: String = config.get[String]("microservice.services.nps-fmn-api.host")
  lazy val npsFMNAPIPort: String = config.get[String]("microservice.services.nps-fmn-api.port")
  lazy val npsFMNAPIToken: String = config.get[String]("microservice.services.nps-fmn-api.token")
  val npsFMNAPIUrl: String = s"$npsFMNAPIProtocol://$npsFMNAPIHost:$npsFMNAPIPort"

  lazy val individualDetailsProtocol: String = config.get[String]("external-url.individual-details.protocol")
  lazy val individualDetailsHost: String = config.get[String]("external-url.individual-details.host")
  lazy val individualDetailsPort: String = config.get[String]("external-url.individual-details.port")
  val individualDetailsServiceUrl: String = s"$individualDetailsProtocol://$individualDetailsHost:$individualDetailsPort"

  def individualDetails: DesApiServiceConfig =
    DesApiServiceConfig(config.get[Configuration]("microservice.services.individual-details"))

}
