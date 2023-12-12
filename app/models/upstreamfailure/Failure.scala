/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.upstreamfailure

import play.api.libs.json.{Format, Json}

final case class Failure(code: String, reason: String)

object Failure {
  implicit val format: Format[Failure] = Json.format[Failure]
}
