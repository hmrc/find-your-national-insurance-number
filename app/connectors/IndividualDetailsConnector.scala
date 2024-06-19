/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.CorrelationId
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.client.HttpClientV2

@Singleton
class IndividualDetailsConnector @Inject()(
                                            val httpClientV2: HttpClientV2,
                                            appConfig:  AppConfig) extends Logging {

  implicit val httpResponseReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

  def getIndividualDetails(nino: String,
                           resolveMerge: String
                          )(implicit hc: HeaderCarrier, ec: ExecutionContext, correlationId: CorrelationId): Future[HttpResponse] = {

    val url = url"${appConfig.individualDetailsServiceUrl}/individuals/details/NINO/${nino.take(8)}?resolveMerge=$resolveMerge"

    httpClientV2.get(url)(desApiHeaders(appConfig.individualDetails))
      .execute(implicitly, ec)
  }

}
