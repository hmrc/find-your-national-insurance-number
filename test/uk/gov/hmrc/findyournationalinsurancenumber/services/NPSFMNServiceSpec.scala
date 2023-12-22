/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

import connectors.DefaultNPSFMNConnector
import models.CorrelationId
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.http.client.RequestBuilder
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import config.AppConfig
import models.nps.NPSFMNRequest

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultNPSFMNConnectorSpec extends PlaySpec with MockitoSugar {

  "DefaultNPSFMNConnector" should {

    "sendLetter" in {

      val mockAppConfig = mock[AppConfig]
      val mockHttpClientV2 = mock[HttpClientV2]
      val mockRequestBuilder = mock[RequestBuilder]

      val nino = "testNino"
      val npsFMNRequest = NPSFMNRequest("test", "test", "01-01-1990", "T16 5KX")
      val correlationId = CorrelationId(UUID.randomUUID())
      val connector = new DefaultNPSFMNConnector(mockHttpClientV2, mockAppConfig)
      val expectedResponse = HttpResponse(Status.OK, "")

      when(mockAppConfig.npsFMNAPIUrl).thenReturn("http://localhost")
      when(mockAppConfig.npsFMNAPIToken).thenReturn("testToken")

      when(mockHttpClientV2.post(any())(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(NPSFMNRequest(any(),any(),any(),any()))).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.setHeader(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(),any())).thenReturn(Future.successful(expectedResponse))

      val result = connector.sendLetter(nino, npsFMNRequest)(HeaderCarrier(), correlationId, global)

      result.map { response =>
        response.status mustBe Status.OK
      }
    }
  }
}