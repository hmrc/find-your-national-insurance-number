/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import com.google.inject.ImplementedBy
import config.AppConfig
import models.CorrelationId
import models.nps.NPSFMNRequest
import play.api.{Logger, Logging}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DefaultNPSFMNConnector])
trait NPSFMNConnector {

  def updateDetails(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier,correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse]
}

@Singleton
class DefaultNPSFMNConnector@Inject() (httpClientV2: HttpClientV2, appConfig: AppConfig)
  extends  NPSFMNConnector
  with MetricsSupport with Logging {

  def updateDetails(nino: String, body: NPSFMNRequest
                   )(implicit hc: HeaderCarrier,correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.npsFMNAPIUrl}/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/$nino"
    val headers = Seq("correlationId" -> correlationId.value.toString,
      "gov-uk-originator-id" -> appConfig.npsFMNAPIOriginatorId)
    
    logger.info(s"[NPSFMNConnector][updateDetails] NPS FMN headers = ${headers}")

    val httpResponse = httpClientV2
      .post(new URL(url))
      .withBody(body)
      .setHeader(headers:_*)
      .execute[HttpResponse]
      .flatMap{ response =>
        Future.successful(response)
      }

    logger.info(s"[NPSFMNConnector][updateDetails] NPS FMN response = ${httpResponse}")

    httpResponse
  }

}