/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models

import play.api.libs.json.{JsPath, Reads, Writes}

import scala.util.matching.Regex


sealed trait FMNIdentifier {
  val value: String
}

object FMNIdentifier {
  final case class NationalInsuranceNumber(value: String)
    extends FMNIdentifier // NINO and CRN are used interchangeably

  final case class TemporaryReferenceNumber(value: String) extends FMNIdentifier

  val NinoAndCRNRegex: Regex =
    """^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D\s]?$""".r
  val TRNRegex: Regex = """^[0-9]{2}[A-Z]{1}[0-9]{5}$""".r

  implicit val reads: Reads[FMNIdentifier] = JsPath.read[String].map {
    case NinoAndCRNRegex(nino) => NationalInsuranceNumber(nino)
    case TRNRegex(trn) => TemporaryReferenceNumber(trn)
  }

  implicit val writes: Writes[FMNIdentifier] = JsPath.write[String].contramap[FMNIdentifier] {
    case NationalInsuranceNumber(nino) => nino
    case TemporaryReferenceNumber(trn) => trn
  }
}
