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

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class NPSFMNServiceSpec extends BaseSpec {

  private val mockNPSFMNConnector = mock[DefaultNPSFMNConnector]


  override implicit lazy val app: Application = localGuiceApplicationBuilder()
    .overrides(
      bind[DefaultNPSFMNConnector].toInstance(mockNPSFMNConnector)
    )

    .build()

  val npsFMNService = app.injector.instanceOf[NPSFMNService]

  override def beforeEach(): Unit =
    reset(mockNPSFMNConnector)



  "sendLetter" must {
    "return 204 response when letter is sent successfully" in {

      implicit val hc: HeaderCarrier = HeaderCarrier()
      implicit val correlationId: CorrelationId = CorrelationId.random
      implicit val ec: ExecutionContext = ExecutionContext.global
      val npsRequest = NPSFMNRequest("test", "test", "01/01/1990", "T16 5KX")
      val nino = "AA000003B"

      when(mockNPSFMNConnector
        .sendLetter(nino, npsRequest))
        .thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))

      val result = npsFMNService.sendLetter(nino, npsRequest)

      whenReady(result) {
        _.status mustBe NO_CONTENT
      }

    }
  }
}
