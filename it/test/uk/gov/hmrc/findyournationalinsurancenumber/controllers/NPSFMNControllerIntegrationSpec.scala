/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

import models.nps.NPSFMNRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AuthConnector, CredentialRole, User}
import uk.gov.hmrc.http.HeaderCarrier
import util.{WireMockHelper, WiremockStub}

import scala.concurrent.{ExecutionContext, Future}

class NPSFMNControllerIntegrationSpec
  extends WiremockStub
     with WireMockHelper
     with MockitoSugar
     with GuiceOneServerPerSuite
     with BeforeAndAfterEach {

  val nino = "SOME_NINO"
  private val baseUrl = s"http://localhost:$port"
  private val url = s"$baseUrl/find-your-national-insurance-number/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/$nino"

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]

  val fakeRetrievalResult: Future[Option[CredentialRole] ~ Option[String]] =
    Future.successful(new~(Some(User), Some("id")))

  override lazy val app: Application = {
    server.start()
    GuiceApplicationBuilder()
      .configure(overrideConfig)
      .overrides(bind[AuthConnector].to(mockAuthConnector))
      .build()
  }

  private val wsClient = app.injector.instanceOf[WSClient]

  override def beforeEach(): Unit = {
    reset(mockAuthConnector)
    server.resetAll()
  }

  "NPS FMN endpoint" should {
    "respond with 202 status on successful letter post" in {
      stubNPS(nino, ACCEPTED)

      when(mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
        any[Predicate],
        any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(fakeRetrievalResult)

      val npsBody = NPSFMNRequest("test", "test", "test", "test")

      val response = await(wsClient.url(url).withHttpHeaders("correlationId" -> "test",
        "gov-uk-originator-id" -> "test").post(Json.toJson(npsBody)))

      response.status mustBe 202
    }

    "respond with 400 status on bad request" in {
      stubNPS(nino, BAD_REQUEST)

      when(mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
        any[Predicate],
        any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(fakeRetrievalResult)

      val npsBody = NPSFMNRequest("test", "test", "test", "test")

      val response = await(wsClient.url(url).withHttpHeaders("correlationId" -> "test",
        "gov-uk-originator-id" -> "test").post(Json.toJson(npsBody)))

      response.status mustBe 400
    }
  }
}
