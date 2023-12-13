/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package config

import play.api.Configuration

final case class DesApiServiceConfig(
    token:        String,
    environment:  String,
    originatorId: String
)

object DesApiServiceConfig {
  def apply(config: Configuration): DesApiServiceConfig =
    DesApiServiceConfig(
      config.get[String]("auth-token"),
      config.get[String]("environment"),
      config.get[String]("originator-id")
    )
}
