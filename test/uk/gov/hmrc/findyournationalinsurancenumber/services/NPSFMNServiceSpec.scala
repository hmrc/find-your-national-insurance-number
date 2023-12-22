/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.services

import connectors.{DefaultNPSFMNConnector, NPSFMNConnector}
import models.CorrelationId
import models.nps.NPSFMNRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.Application
import play.api.http.Status.NO_CONTENT
import services.{NPSFMNService, NPSFMNServiceImpl}
import uk.gov.hmrc.findyournationalinsurancenumber.util.BaseSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import play.api.inject.bind

import scala.concurrent.{ExecutionContext, Future}

class NPSFMNServiceSpec extends BaseSpec {

  private val mockNPSFMNConnector = mock[DefaultNPSFMNConnector]

  override implicit lazy val app: Application = localGuiceApplicationBuilder()
    .overrides(
      bind[DefaultNPSFMNConnector].toInstance(mockNPSFMNConnector)
    )
    .build()

  override def beforeEach(): Unit =
    reset(mockNPSFMNConnector)

  val npsFMNService = inject[NPSFMNServiceImpl]

  "sendLetter" must {
    "return 204 response when letter is sent successfully" in {

      when(mockNPSFMNConnector.sendLetter("test", NPSFMNRequest("test", "test", "01/01/1990", "T16 5KX"))(any[HeaderCarrier](), any[CorrelationId](), any[ExecutionContext]()))
        .thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))

      val result = npsFMNService.sendLetter("test", NPSFMNRequest("test", "test", "01/01/1990", "T16 5KX"))(implicitly, implicitly)

      whenReady(result) {
        _.status mustBe NO_CONTENT
      }

    }
  }
}
