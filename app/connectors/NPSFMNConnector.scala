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
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DefaultNPSFMNConnector])
trait NPSFMNConnector {

  def sendLetter(nino: String, npsFMNRequest: NPSFMNRequest
                   )(implicit hc: HeaderCarrier,correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse]
}

@Singleton
class DefaultNPSFMNConnector @Inject()(httpClientV2: HttpClientV2, appConfig: AppConfig)
  extends  NPSFMNConnector with Logging {

  def sendLetter(nino: String, body: NPSFMNRequest
                   )(implicit hc: HeaderCarrier,correlationId: CorrelationId, ec: ExecutionContext): Future[HttpResponse] = {
    val url = s"${appConfig.npsFMNAPIUrl}/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/$nino"

    val headers = Seq(
      (play.api.http.HeaderNames.CONTENT_TYPE, MimeTypes.JSON),
      (play.api.http.HeaderNames.AUTHORIZATION, s"Basic ${appConfig.npsFMNAPIToken}"),
      (appConfig.npsFMNAPICorrelationIdKey, correlationId.value.toString),
      (appConfig.npsFMNAPIOriginatorIdKey, appConfig.npsFMNAPIOriginatorIdValue)
    )

    logger.info(s"[NPSFMNConnector][sendLetter] NPS FMN headers = ${headers}")

    val httpResponse = httpClientV2
      .post(new URL(url))
      .withBody(body)
      .setHeader(headers:_*)
      .execute[HttpResponse]
      .flatMap{ response =>
        logger.info(s"BE [NPSFMNConnector][sendLetter] NPS FMN response = ${response}")
        Future.successful(response)
      }

    httpResponse
  }

}