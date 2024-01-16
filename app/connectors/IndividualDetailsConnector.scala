/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.CorrelationId
import play.api.Logging
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IndividualDetailsConnector @Inject()(
  val httpClient: HttpClient,
  appConfig:  AppConfig) extends Logging {

  def getIndividualDetails(nino: String, resolveMerge: String
                          )(implicit hc: HeaderCarrier, ec: ExecutionContext, correlationId: CorrelationId): Future[HttpResponse] = {
    val url = s"${appConfig.individualDetailsServiceUrl}/individuals/details/NINO/${nino.take(8)}?resolveMerge=$resolveMerge"
    httpClient.GET[HttpResponse](url)(implicitly, desApiHeaders(appConfig.individualDetails), implicitly)
  }

}
