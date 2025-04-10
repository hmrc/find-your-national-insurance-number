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

package services

import connectors.DefaultNPSFMNConnector
import models.CorrelationId
import models.nps.NPSFMNRequest
import org.mockito.Mockito.{reset, when}
import play.api.Application
import play.api.http.Status.NO_CONTENT
import play.api.inject.bind
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import util.BaseSpec

import scala.concurrent.{ExecutionContext, Future}

class NPSFMNServiceSpec extends BaseSpec {

  private val mockNPSFMNConnector = mock[DefaultNPSFMNConnector]


  override implicit lazy val app: Application = localGuiceApplicationBuilder()
    .overrides(
      bind[DefaultNPSFMNConnector].toInstance(mockNPSFMNConnector)
    )

    .build()

  val npsFMNService: NPSFMNService = app.injector.instanceOf[NPSFMNService]

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
