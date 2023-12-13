/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.Json

final case class AddressLine(value: String) extends AnyVal

object AddressLine {
  implicit val format = Json.valueFormat[AddressLine]
}
