/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package controllers

import config.{AppConfig, DesApiServiceConfig}
import connectors.IndividualDetailsConnector
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
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class IndividualsDetailsControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfter {

import IndividualsDetailsControllerSpec._

  "getIndividualDetails" must {

    "should return OK" in {
      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe OK
      }
    }

    "should return InternalServerError when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 500
      }
    }

    "should return BadRequest when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 400
      }
    }

    "should return Unauthorized when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(UNAUTHORIZED, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 401
      }
    }

    "should return NotFound when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(NOT_FOUND, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 404
      }
    }

    "should return NotImplemented when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(NOT_IMPLEMENTED, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 501
      }
    }

    "should return ServiceUnavailable when failure occurs" in {
      when(mockIndividualDetailsConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(SERVICE_UNAVAILABLE, "" )))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 503
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
    ("Content-Type" -> "application/json"),
    ("Authorization" -> "Bearer 123")
  )

  private val mockHttpClient = mock[HttpClient]
  private val mockAppConfig = mock[AppConfig]
  private val mockDesApiServiceConfig = mock[DesApiServiceConfig]
  private val mockAuthConnector = mock[AuthConnector]
  private val mockIndividualDetailsConnector = new IndividualDetailsConnector(mockHttpClient, mockAppConfig)

  when(mockDesApiServiceConfig.token).thenReturn("test")
  when(mockDesApiServiceConfig.environment).thenReturn("test")
  when(mockDesApiServiceConfig.originatorId).thenReturn("test")
  when(mockAppConfig.individualDetails).thenReturn(mockDesApiServiceConfig)

  when(mockHttpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
    .thenReturn(Future.successful(HttpResponse(OK, "")))
  when(mockAppConfig.individualDetailsServiceUrl).thenReturn("test")

  val retrievalResult: Future[Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(Some(User), Some("id")))

  when(
    mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
      any[Predicate],
      any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
    .thenReturn(retrievalResult)

  val modules: Seq[GuiceableModule] =
    Seq(
      bind[IndividualDetailsConnector].toInstance(mockIndividualDetailsConnector),
      bind[AuthConnector].toInstance(mockAuthConnector)
    )

  val application: Application = new GuiceApplicationBuilder()
    .configure(conf = "auditing.enabled" -> false, "metrics.enabled" -> false, "metrics.jvm" -> false).
    overrides(modules: _*).build()

  private val controller = application.injector.instanceOf[IndividualsDetailsController]

}

