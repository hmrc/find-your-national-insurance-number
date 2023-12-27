/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

import connectors.IndividualDetailsConnector
import controllers.IndividualsDetailsController
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
import play.api.test.Helpers._
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class IndividualsDetailsControllerSpec extends AnyWordSpec with Matchers with MockitoSugar with BeforeAndAfter {

  import IndividualsDetailsControllerSpec._

  "getIndividualDetails" must {
    "should return OK" in {

      when(mockIndividualDetailsConnector.getIndividualDetails(
        nino, resolveMerge))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe OK
      }
    }

    "should return InternalServerError when failure occurs" in {
      when(mockIndividualDetailsConnector.getIndividualDetails(
        nino, resolveMerge))
        .thenReturn(Future.failed(new Exception("failed")))

      val result = controller.getIndividualDetails(nino, resolveMerge)(fakeRequestWithAuth)

      whenReady(result) { _ =>
        status(result) mustBe 500
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

  private val mockIndividualDetailsConnector = mock[IndividualDetailsConnector]
  private val mockAuthConnector = mock[AuthConnector]

  val retrievalResult: Future[Option[String] ~ Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(new~(Some("nino"), Some(User)), Some("id")))

  when(
    mockAuthConnector.authorise[Option[String] ~ Option[CredentialRole] ~ Option[String]](
      any[Predicate],
      any[Retrieval[Option[String] ~ Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
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

