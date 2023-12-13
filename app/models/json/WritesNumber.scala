/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.json

import play.api.libs.json.{JsNumber, Writes}

object WritesNumber {
  def apply[T](f: T => Int): Writes[T] = {
    Writes(f.andThen(JsNumber(_)))
  }
}
