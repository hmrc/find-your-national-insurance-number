/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.individualdetails

import play.api.libs.json.{Json, OFormat}

final case class NameList(name: Option[List[Name]])

object NameList {
  implicit val format: OFormat[NameList] = Json.format[NameList]
}
