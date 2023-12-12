/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models

import java.util.UUID

final case class CorrelationId(value: UUID) extends AnyVal

object CorrelationId {
  def random: CorrelationId = CorrelationId(UUID.randomUUID())
}
