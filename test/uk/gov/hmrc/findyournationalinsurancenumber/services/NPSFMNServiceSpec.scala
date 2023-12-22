/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.services

import connectors.NPSFMNConnector
import models.nps.NPSFMNRequest
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, when}
import play.api.Application
import services.NPSFMNService
import uk.gov.hmrc.findyournationalinsurancenumber.util.BaseSpec
import uk.gov.hmrc.http.HttpResponse
import play.api.inject.bind

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NPSFMNServiceSpec extends BaseSpec {

  private val mockNPSFMNConnector = mock[NPSFMNConnector]

  override implicit lazy val app: Application = localGuiceApplicationBuilder()
    .overrides(
      bind[NPSFMNConnector].toInstance(mockNPSFMNConnector)
    )
    .build()

  override def beforeEach(): Unit =
    reset(mockNPSFMNConnector)

  val npsFMNService = inject[NPSFMNService]

  "sendLetter" must {
    "return 200 response when letter is sent successfully" in {

      when(mockNPSFMNConnector.sendLetter(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, "success")))

      npsFMNService.sendLetter("test", NPSFMNRequest("test", "test", "01/01/1990", "T16 5KX"))(implicitly, global).map { result =>
        result.status mustBe 200
      }(global)

    }
  }
}
