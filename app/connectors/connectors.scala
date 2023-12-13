/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

import config.DesApiServiceConfig
import models.CorrelationId
import uk.gov.hmrc.http.HeaderCarrier

package object connectors {
  def desApiHeaders(
                     config: DesApiServiceConfig
                   )(implicit hc: HeaderCarrier, correlationId: CorrelationId): HeaderCarrier = {
    val headers = Seq(
      "Authorization" -> s"Bearer ${config.token}",
      "CorrelationId" -> correlationId.value.toString,
      "Content-Type" -> "application/json",
      "Environment" -> config.environment,
      "OriginatorId" -> config.originatorId
    )

    hc.withExtraHeaders(headers: _*)
  }
}
