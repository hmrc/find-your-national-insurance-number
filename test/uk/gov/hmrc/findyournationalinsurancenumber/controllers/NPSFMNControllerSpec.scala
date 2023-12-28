/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

import config.AppConfig
import connectors.NPSFMNConnector
import controllers.NPSFMNController
import models.CorrelationId
import models.nps.NPSFMNRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.i18n.MessagesApi
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{NPSFMNService, NPSFMNServiceImpl}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class NPSFMNControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfter {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val correlationId: CorrelationId = CorrelationId.random
  implicit val ec: ExecutionContext = ExecutionContext.global
  val nino = "AA000003B"

  private val fakeRequestWithAuth = FakeRequest("GET", "/").withHeaders(
    ("Content-Type" -> "application/json"),
    ("Authorization" -> "Bearer 123")
  )

  private val mockMessagesApi = mock[MessagesApi]
  private val mockControllerComponents = mock[ControllerComponents]
  private val mockAuthConnector = mock[AuthConnector]
  private val mockNPSFMNService = new NPSFMNServiceImpl(mock[NPSFMNConnector], mock[AppConfig])


  val retrievalResult: Future[Option[String] ~ Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(new~(Some("nino"), Some(User)), Some("id")))

  when(
    mockAuthConnector.authorise[Option[String] ~ Option[CredentialRole] ~ Option[String]](
      any[Predicate],
      any[Retrieval[Option[String] ~ Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
    .thenReturn(retrievalResult)

  val modules: Seq[GuiceableModule] =
    Seq(
      bind[MessagesApi].toInstance(mockMessagesApi),
      bind[ControllerComponents].toInstance(mockControllerComponents),
      bind[AuthConnector].toInstance(mockAuthConnector),
      bind[NPSFMNService].toInstance(mockNPSFMNService)
    )

  val application: Application = new GuiceApplicationBuilder()
    .configure(conf = "auditing.enabled" -> false, "metrics.enabled" -> false, "metrics.jvm" -> false).
    overrides(modules: _*).build()

  private val controller = application.injector.instanceOf[NPSFMNController]

  "sendLetter" should {
    "return 202 Accepted when the service returns a 202 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(202, "Accepted")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe ACCEPTED
    }

    "return 400 BadRequest when the service returns a 400 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(400, "Bad Request")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe BAD_REQUEST
    }

    "return 401 Unauthorized when the service returns a 401 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(401, "Unauthorized")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe UNAUTHORIZED
    }

    "return 404 NotFound when the service returns a 404 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(404, "Not Found")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe NOT_FOUND
    }

    "return 500 InternalServerError when the service returns a 500 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(500, "Internal Server Error")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe INTERNAL_SERVER_ERROR
    }

    "return 501 NotImplemented when the service returns a 501 response" in {
      val npsFMNRequest = NPSFMNRequest("line1", "line2", "line3", "line4")
      val httpResponse = uk.gov.hmrc.http.HttpResponse(501, "Not Implemented")

      when(mockNPSFMNService.sendLetter(any(), NPSFMNRequest(any(),any(),any(),any())))
        .thenReturn(Future.successful(httpResponse))

      val result = controller.sendLetter(nino)(fakeRequestWithAuth.withBody(Json.toJson(npsFMNRequest)))

      status(result) mustBe NOT_IMPLEMENTED
    }

  }
}
