/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import com.google.inject.ImplementedBy
import config.AppConfig
import models.CorrelationId
import models.nps.NPSFMNRequest
import play.api.http.MimeTypes
import play.api.{Logger, Logging}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.net.URL
import java.time.Instant.now
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
//    val headers = Seq(
//      "X-Correlation-ID" -> correlationId.value.toString,
//      "gov-uk-originator-id" -> appConfig.npsFMNAPIOriginatorId,
//      "Authorization" -> appConfig.npsFMNAPIToken)

    val headers = Seq(
      (play.api.http.HeaderNames.CONTENT_TYPE, MimeTypes.JSON),
      (play.api.http.HeaderNames.ACCEPT, MimeTypes.JSON),
      (play.api.http.HeaderNames.AUTHORIZATION, s"Bearer ${appConfig.npsFMNAPIToken}"),
      ("X-Correlation-ID" -> correlationId.value.toString),
      ("environment", "ist0"),
      ("Gov-Uk-Originator-Id", appConfig.npsFMNAPIOriginatorId)
    )

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