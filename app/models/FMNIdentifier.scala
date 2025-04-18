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

package models

import play.api.libs.json.{JsPath, Reads, Writes}

import scala.util.matching.Regex


sealed trait FMNIdentifier {
  val value: String
}

object FMNIdentifier {
  private final case class NationalInsuranceNumber(value: String)
    extends FMNIdentifier // NINO and CRN are used interchangeably

  private final case class TemporaryReferenceNumber(value: String) extends FMNIdentifier

  private val NinoAndCRNRegex: Regex =
    """^((?!(BG|GB|KN|NK|NT|TN|ZZ)|([DFIQUV])[A-Z]|[A-Z]([DFIOQUV]))[A-Z]{2})[0-9]{6}[A-D\s]?$""".r
  private val TRNRegex: Regex = """^[0-9]{2}[A-Z][0-9]{5}$""".r

  implicit val reads: Reads[FMNIdentifier] = JsPath.read[String].map {
    case NinoAndCRNRegex(nino) => NationalInsuranceNumber(nino)
    case TRNRegex(trn)         => TemporaryReferenceNumber(trn)
    case _                     => throw new IllegalArgumentException("Unknown identifier")
  }

  implicit val writes: Writes[FMNIdentifier] = JsPath.write[String].contramap[FMNIdentifier] {
    case NationalInsuranceNumber(nino) => nino
    case TemporaryReferenceNumber(trn) => trn
  }
}
