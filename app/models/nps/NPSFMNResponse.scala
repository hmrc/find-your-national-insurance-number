/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.nps

import play.api.libs.json.{Format, Json}

case class AppStatusMessageList(appStatusMessage: List[String] = List.empty)
case class JsonServiceError(
                             requestURL: String,
                             message: String,
                             appStatusMessageCount: Int,
                             appStatusMessageList: AppStatusMessageList
                           )
case class NPSFMNResponse(jsonServiceError: JsonServiceError)

object NPSFMNResponse {
  implicit val appStatusMessageListformat: Format[AppStatusMessageList] = Json.format[AppStatusMessageList]
  implicit val jsonServiceErrorformat: Format[JsonServiceError] = Json.format[JsonServiceError]
  implicit val npsFMNResponseformat: Format[NPSFMNResponse] = Json.format[NPSFMNResponse]
}

sealed trait NPSFMNServiceResponse
final case class LetterIssuedResponse() extends NPSFMNServiceResponse
final case class RLSDLONFAResponse(status: Int, message: String) extends NPSFMNServiceResponse
final case class TechnicalIssueResponse(status: Int, message: String) extends NPSFMNServiceResponse

