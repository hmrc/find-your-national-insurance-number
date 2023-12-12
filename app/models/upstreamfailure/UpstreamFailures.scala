/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.upstreamfailure

import play.api.libs.json.{Format, Json}

final case class UpstreamFailures(failures: List[Failure])

object UpstreamFailures {
  implicit val format: Format[UpstreamFailures] = Json.format[UpstreamFailures]
}
