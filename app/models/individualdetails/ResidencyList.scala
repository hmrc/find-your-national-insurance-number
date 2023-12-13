/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.individualdetails

import play.api.libs.json.{Json, OFormat}

final case class ResidencyList(residency: Option[List[Residency]])

object ResidencyList {
  implicit val format: OFormat[ResidencyList] = Json.format[ResidencyList]
}
