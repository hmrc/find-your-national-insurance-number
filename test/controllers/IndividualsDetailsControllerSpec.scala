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

package controllers

import config.{AppConfig, DesApiServiceConfig}
import connectors.IndividualDetailsConnector
import controllers.IndividualsDetailsControllerSpec.*
import models.CorrelationId
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures.whenReady
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class IndividualsDetailsControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfter {

  "getIndividualDetails" must {

    "return OK(200)" in {
      setupHttpClientResponse(HttpResponse(OK, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe OK
      }
    }

    "return InternalServerError(500)" in {
      setupHttpClientResponse(HttpResponse(INTERNAL_SERVER_ERROR, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return BadRequest(400)" in {
      setupHttpClientResponse(HttpResponse(BAD_REQUEST, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe BAD_REQUEST
      }
    }

    "return Unauthorized(401)" in {
      setupHttpClientResponse(HttpResponse(UNAUTHORIZED, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe UNAUTHORIZED
      }
    }

    "return NotFound(404)" in {
      setupHttpClientResponse(HttpResponse(NOT_FOUND, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe NOT_FOUND
      }
    }

    "return NotImplemented(501)" in {
      setupHttpClientResponse(HttpResponse(NOT_IMPLEMENTED, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe NOT_IMPLEMENTED
      }
    }

    "return ServiceUnavailable(503)" in {
      setupHttpClientResponse(HttpResponse(SERVICE_UNAVAILABLE, ""))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      whenReady(result) { _ =>
        status(result) mustBe SERVICE_UNAVAILABLE
      }
    }
  }
}

object IndividualsDetailsControllerSpec {
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: CorrelationId = CorrelationId.random
  implicit val ec: ExecutionContext = ExecutionContext.global
  val nino = "AA000003B"
  val resolveMerge = "test"

  private val fakeRequestWithAuth = FakeRequest("GET", "/").withHeaders(
    "Content-Type" -> "application/json",
    "Authorization" -> "Bearer 123"
  )

  private val mockHttpClient = mock[HttpClientV2]
  private val mockRequestBuilder = mock[RequestBuilder]
  private val mockAppConfig = mock[AppConfig]
  private val mockDesApiServiceConfig = mock[DesApiServiceConfig]
  private val mockAuthConnector = mock[AuthConnector]

  private val mockIndividualDetailsConnector = new IndividualDetailsConnector(mockHttpClient, mockAppConfig)

  when(mockDesApiServiceConfig.token).thenReturn("test-token")
  when(mockDesApiServiceConfig.environment).thenReturn("test-env")
  when(mockDesApiServiceConfig.originatorId).thenReturn("test-originator")
  when(mockAppConfig.individualDetails).thenReturn(mockDesApiServiceConfig)
  when(mockAppConfig.individualDetailsServiceUrl).thenReturn("http://test")

  when(mockHttpClient.get(any())(any())).thenReturn(mockRequestBuilder)
  when(mockRequestBuilder.setHeader()).thenReturn(mockRequestBuilder)

  def setupHttpClientResponse(response: HttpResponse): Unit = {
    when(mockRequestBuilder.execute[HttpResponse](any(), any()))
      .thenReturn(Future.successful(response))
    ()
  }

  val retrievalResult: Future[Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(Some(User), Some("id")))

  when(
    mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
      any[Predicate],
      any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext])
  ).thenReturn(retrievalResult)

  val modules: Seq[GuiceableModule] = Seq(
    bind[IndividualDetailsConnector].toInstance(mockIndividualDetailsConnector),
    bind[AuthConnector].toInstance(mockAuthConnector)
  )

  val application: Application = new GuiceApplicationBuilder()
    .configure("auditing.enabled" -> false, "metrics.enabled" -> false, "metrics.jvm" -> false)
    .overrides(modules: _*)
    .build()

  val controller: IndividualsDetailsController = application.injector.instanceOf[IndividualsDetailsController]
}
