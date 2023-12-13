/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.json

import play.api.libs.json.{JsString, Writes}

object WritesString {
  def apply[T](f: T => String): Writes[T] = {
    Writes(f.andThen(JsString(_)))
  }
}
