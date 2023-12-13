/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.nps

import org.apache.commons.lang3.StringUtils
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.ws.BodyWritable

case class NPSFMNRequest(firstForename: String, surname: String, dateOfBirth: String, postCode: String)

object NPSFMNRequest {

  def empty = NPSFMNRequest(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY)

  implicit val writes: Writes[NPSFMNRequest] = Json.writes[NPSFMNRequest]
  implicit def jsonBodyWritable[T](implicit writes: Writes[T],
       jsValueBodyWritable: BodyWritable[JsValue]
      ): BodyWritable[T] = jsValueBodyWritable.map(writes.writes)
}