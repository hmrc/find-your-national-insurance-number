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
  def sendLetter(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier, correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse]
}

class NPSFMNServiceImpl @Inject()(connector: NPSFMNConnector, config: AppConfig)(implicit val ec: ExecutionContext)
  extends NPSFMNService with Logging {

  def sendLetter(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier, correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse] = {
      connector.sendLetter(nino, npsFMNRequest)
  }

}
