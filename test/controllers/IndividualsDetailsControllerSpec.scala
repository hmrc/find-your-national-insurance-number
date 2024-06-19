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

package controllers

import config.{AppConfig, DesApiServiceConfig}
import connectors.IndividualDetailsConnector
import models.CorrelationId
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.http.Status.OK
import play.api.i18n.MessagesApi
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits.*

import scala.concurrent.{ExecutionContext, Future}

class IndividualsDetailsControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfter {

import IndividualsDetailsControllerSpec._

  "getIndividualDetails" must {

    "return OK(200)" in {
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe OK
    }

    "return BadRequest(400) when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe BAD_REQUEST
    }

    "return Unauthorised(401) when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(UNAUTHORIZED, "")))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe UNAUTHORIZED
    }

    "return Notfound(404) when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(NOT_FOUND, "")))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe NOT_FOUND
    }

    "return InternalServerError(500) when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "")))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }

    "return NotImplemented(501) when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(NOT_IMPLEMENTED, "")))
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)
      status(result) mustBe NOT_IMPLEMENTED
    }

  }
}

object IndividualsDetailsControllerSpec {
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: CorrelationId = CorrelationId.random
  implicit val ec: ExecutionContext = ExecutionContext.global
  val nino = "AA000003B"
  val resolveMerge = "Y"

  private val fakeRequestWithAuth = FakeRequest("GET", "/").withHeaders(
    "Content-Type" -> "application/json",
    "Authorization" -> "Bearer 123"
  )

  private val mockHttpClientV2 = mock[HttpClientV2]
  private val mockAppConfig = mock[AppConfig]
  private val mockDesApiServiceConfig = mock[DesApiServiceConfig]
  private val mockMessagesApi = mock[MessagesApi]
  private val mockControllerComponents = mock[ControllerComponents]
  private val mockAuthConnector = mock[AuthConnector]
  private val mockIndividualDetailsConnector = mock[IndividualDetailsConnector]

  when(mockAppConfig.individualDetailsServiceUrl).thenReturn("http://localhost:14011/")
  when(mockIndividualDetailsConnector.getIndividualDetails(any(), any())(any(), any(), any())).thenReturn(Future.successful(HttpResponse(OK, "")))

  when(mockDesApiServiceConfig.token).thenReturn("test")
  when(mockDesApiServiceConfig.environment).thenReturn("test")
  when(mockDesApiServiceConfig.originatorId).thenReturn("test")
  when(mockAppConfig.individualDetails).thenReturn(mockDesApiServiceConfig)

  val requestBuilder: RequestBuilder = mock[RequestBuilder]
  when(requestBuilder.execute(HttpReads.Implicits.readRaw, ec)).thenReturn(Future.successful(HttpResponse(OK, "")))

  when(mockHttpClientV2.get(any())(any())).thenReturn(requestBuilder)

  when(mockAppConfig.individualDetailsServiceUrl).thenReturn("http://localhost:14011/")

  val retrievalResult: Future[Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(Some(User), Some("id")))

  when(
    mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
      any[Predicate],
      any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
    .thenReturn(retrievalResult)

  val modules: Seq[GuiceableModule] =
    Seq(
      bind[MessagesApi].toInstance(mockMessagesApi),
      bind[ControllerComponents].toInstance(mockControllerComponents),
      bind[IndividualDetailsConnector].toInstance(mockIndividualDetailsConnector),
      bind[AuthConnector].toInstance(mockAuthConnector)
    )

  val application: Application = new GuiceApplicationBuilder()
    .configure(conf = "auditing.enabled" -> false, "metrics.enabled" -> false, "metrics.jvm" -> false)
    .overrides(modules: _*)
    .build()

  private val controller = application.injector.instanceOf[IndividualsDetailsController]

}

