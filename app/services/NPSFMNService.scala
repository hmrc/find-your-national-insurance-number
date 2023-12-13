/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package services

import com.google.inject.ImplementedBy
import config.AppConfig
import connectors.NPSFMNConnector
import models.CorrelationId
import models.nps.NPSFMNRequest
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[NPSFMNServiceImpl])
trait NPSFMNService {
  def updateDetails(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]
}

class NPSFMNServiceImpl @Inject()(connector: NPSFMNConnector, config: AppConfig)(implicit val ec: ExecutionContext)
  extends NPSFMNService with Logging {

  def updateDetails(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
    connector.updateDetails(nino, npsFMNRequest)
  }

}
