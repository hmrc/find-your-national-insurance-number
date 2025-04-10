/*
 * Copyright 2024 HM Revenue & Customs
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

import config.AppConfig
import models.CorrelationId
import models.nps.NPSFMNRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.*
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}

import java.net.URL
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class DefaultNPSFMNConnectorSpec extends PlaySpec with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
  val nino: String = "testNino"
  val npsFMNRequest: NPSFMNRequest = mock[NPSFMNRequest]
  val httpResponse: HttpResponse = HttpResponse(OK, "testResponse")

  "DefaultNPSFMNConnector" should {

    "sendLetter" should {
      "return a Ok successful response" in {
        val mockHttpClientV2 = mock[HttpClientV2]
        val mockAppConfig = mock[AppConfig]
        val connector = new DefaultNPSFMNConnector(mockHttpClientV2, mockAppConfig)

        val requestBuilder = mock[RequestBuilder]
        when(mockHttpClientV2.post(any[URL]())(any())).thenReturn(requestBuilder)
        when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
        when(requestBuilder.setHeader(any[(String, String)]())).thenReturn(requestBuilder)

        when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext])).thenReturn(Future.successful(httpResponse))
        when(mockAppConfig.npsFMNAPIUrl).thenReturn("https://localhost:14011/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual")
        when(mockAppConfig.npsFMNAPIToken).thenReturn("testToken")
        when(mockAppConfig.npsFMNAPICorrelationIdKey).thenReturn("testCorrelationIdKey")
        when(mockAppConfig.npsFMNAPIOriginatorIdKey).thenReturn("testOriginatorIdKey")
        when(mockAppConfig.npsFMNAPIOriginatorIdValue).thenReturn("testOriginatorIdValue")

        val result = connector.sendLetter(nino, npsFMNRequest)

        result.map { response =>
          response.status mustBe OK
          response.body mustBe "testResponse"
        }
      }

      "return a failed response" in {
        val mockHttpClientV2 = mock[HttpClientV2]
        val mockAppConfig = mock[AppConfig]
        val connector = new DefaultNPSFMNConnector(mockHttpClientV2, mockAppConfig)

        val requestBuilder = mock[RequestBuilder]
        when(mockHttpClientV2.post(any[URL]())(any())).thenReturn(requestBuilder)
        when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
        when(requestBuilder.setHeader(any[(String, String)]())).thenReturn(requestBuilder)

        when(requestBuilder.execute(any[HttpReads[HttpResponse]], any[ExecutionContext])).thenReturn(Future.failed(new Exception("testException")))
        when(mockAppConfig.npsFMNAPIUrl).thenReturn("https://localhost:14011/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual")
        when(mockAppConfig.npsFMNAPIToken).thenReturn("testToken")
        when(mockAppConfig.npsFMNAPICorrelationIdKey).thenReturn("testCorrelationIdKey")
        when(mockAppConfig.npsFMNAPIOriginatorIdKey).thenReturn("testOriginatorIdKey")
        when(mockAppConfig.npsFMNAPIOriginatorIdValue).thenReturn("testOriginatorIdValue")

        val result = connector.sendLetter(nino, npsFMNRequest)

        result.failed.map { exception =>
          exception.getMessage mustBe "testException"
        }
      }
    }
  }
}
