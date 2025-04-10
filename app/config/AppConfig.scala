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

package config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  val appName: String = config.get[String]("appName")

  lazy val npsFMNAPICorrelationIdKey: String = config.get[String]("microservice.services.nps-fmn-api.correlationId.key")
  lazy val npsFMNAPIOriginatorIdKey: String = config.get[String]("microservice.services.nps-fmn-api.govUkOriginatorId.key")
  lazy val npsFMNAPIOriginatorIdValue: String = config.get[String]("microservice.services.nps-fmn-api.govUkOriginatorId.value")

  private lazy val npsFMNAPIProtocol: String = config.get[String]("microservice.services.nps-fmn-api.protocol")
  private lazy val npsFMNAPIHost: String = config.get[String]("microservice.services.nps-fmn-api.host")
  private lazy val npsFMNAPIPort: String = config.get[String]("microservice.services.nps-fmn-api.port")
  lazy val npsFMNAPIToken: String = config.get[String]("microservice.services.nps-fmn-api.token")
  val npsFMNAPIUrl: String = s"$npsFMNAPIProtocol://$npsFMNAPIHost:$npsFMNAPIPort"

  private lazy val individualDetailsProtocol: String = config.get[String]("external-url.individual-details.protocol")
  private lazy val individualDetailsHost: String = config.get[String]("external-url.individual-details.host")
  private lazy val individualDetailsPort: String = config.get[String]("external-url.individual-details.port")
  val individualDetailsServiceUrl: String = s"$individualDetailsProtocol://$individualDetailsHost:$individualDetailsPort"

  def individualDetails: DesApiServiceConfig =
    DesApiServiceConfig(config.get[Configuration]("microservice.services.individual-details"))

}
