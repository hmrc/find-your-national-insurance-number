/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import models._
import models.nps.NPSFMNRequest
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, Injecting}
import config.AppConfig
import uk.gov.hmrc.http.client.HttpClientV2
import util.{WiremockStub, WireMockHelper}

import java.util.UUID

class NPSFMNConnectorSpec
  extends WiremockStub
    with WireMockHelper
    with MockitoSugar
    with DefaultAwaitTimeout
    with Injecting {

  override implicit lazy val app: Application = app(
    Map("microservice.services.nps-fmn-api.port" -> server.port(),
    )
  )

  val nino = "nino"

  val jsonInternalServerError = s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/${nino}",
                |    "message": "GENERIC_SERVER_ERROR",
                |    "appStatusMessageCount": 1,
                |    "appStatusMessageList": {
                |      "appStatusMessage": [
                |        "Internal Server Error"
                |      ]
                |    }
                |  }
                |}
                |""".stripMargin

  val jsonResourceNotFound =  s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/${nino}",
                |    "message": "RESOURCE_NOT_FOUND",
                |    "appStatusMessageCount": 1,
                |    "appStatusMessageList": {
                |      "appStatusMessage": [
                |        "65370"
                |      ]
                |    }
                |  }
                |}
                |""".stripMargin

  val jsonNotFound = s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/${nino}",
                |    "message": "BAD_REQUEST",
                |    "appStatusMessageCount": 1,
                |    "appStatusMessageList": {
                |      "appStatusMessage": [
                |        "63471"
                |      ]
                |    }
                |  }
                |}
                |""".stripMargin


  trait SpecSetup {

    def url(nino: String): String

    lazy val connector = {
      val httpClient2 = app.injector.instanceOf[HttpClientV2]
      val config = app.injector.instanceOf[AppConfig]
      new DefaultNPSFMNConnector(httpClient2, config)
    }
  }

  "NPS FMN Connector" must {

    trait LocalSetup extends SpecSetup {
      def url(nino: String) = s"/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/${nino}"
    }

    "return Ok (200) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      val body = mock[NPSFMNRequest]
      stubPost(url(nino), OK, Some(Json.toJson(body).toString()), Some(""))
      val result = connector.sendLetter(nino, body).futureValue.leftSideValue
      result.status mustBe OK
      result.body mustBe ""
    }

    "return NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      val body = mock[NPSFMNRequest]
      stubPost(url(nino), NOT_FOUND, Some(Json.toJson(body).toString()), Some(jsonNotFound))
      val result = connector.sendLetter(nino, body).futureValue.leftSideValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonNotFound
    }

    "return RESOURCE_NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      val body = mock[NPSFMNRequest]
      stubPost(url(nino), NOT_FOUND, Some(Json.toJson(body).toString()), Some(jsonResourceNotFound))
      val result = connector.sendLetter(nino, body).futureValue.leftSideValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonResourceNotFound
    }

    "return INTERNAL_SERVER_ERROR (500) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      val body = mock[NPSFMNRequest]
      stubPost(url(nino), INTERNAL_SERVER_ERROR, Some(Json.toJson(body).toString()), Some(jsonInternalServerError))
      val result = connector.sendLetter(nino, body).futureValue.leftSideValue
      result.status mustBe INTERNAL_SERVER_ERROR
      result.body mustBe jsonInternalServerError
    }
  }

}