/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.individualdetails

import models.json.WritesNumber
import play.api.libs.json._

import java.time.LocalDate

final case class ResidencySequenceNumber(value: Int) extends AnyVal
object ResidencySequenceNumber {
  implicit val format = Json.valueFormat[ResidencySequenceNumber]
}
sealed trait ResidencyStatusFlag

object ResidencyStatusFlag {
  object UK     extends ResidencyStatusFlag
  object Abroad extends ResidencyStatusFlag

  implicit val reads: Reads[ResidencyStatusFlag] = JsPath
    .read[Int]
    .map {
      case 0 => UK
      case 1 => Abroad
    }
  implicit val writes: Writes[ResidencyStatusFlag] = WritesNumber[ResidencyStatusFlag] {
    case UK     => 0
    case Abroad => 1
  }
}

final case class Residency(
    residencySequenceNumber: ResidencySequenceNumber,
    dateLeavingUK:           Option[LocalDate],
    dateReturningUK:         Option[LocalDate],
    residencyStatusFlag:     ResidencyStatusFlag
)

object Residency {
  implicit val format: OFormat[Residency] = Json.format[Residency]
}
