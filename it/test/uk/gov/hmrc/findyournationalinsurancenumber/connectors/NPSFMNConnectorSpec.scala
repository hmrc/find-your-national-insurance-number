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

package uk.gov.hmrc.findyournationalinsurancenumber.connectors

import config.AppConfig
import connectors.DefaultNPSFMNConnector
import models.*
import models.nps.NPSFMNRequest
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, Injecting}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import util.{WireMockHelper, WiremockStub}

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

  val jsonInternalServerError: String = s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/$nino",
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

  val jsonResourceNotFound: String =  s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/$nino",
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

  val jsonNotFound: String = s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/itmp/find-my-nino/api/v1/individual/$nino",
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

    lazy val connector: DefaultNPSFMNConnector = {
      val httpClient2 = app.injector.instanceOf[HttpClientV2]
      val config = app.injector.instanceOf[AppConfig]
      new DefaultNPSFMNConnector(httpClient2, config)
    }
  }

  "NPS FMN Connector" must {

    trait LocalSetup extends SpecSetup {
      def url(nino: String) = s"/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/$nino"
    }

    "return Ok (200) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
      val body: NPSFMNRequest = mock[NPSFMNRequest]
      stubPost(url(nino), OK, Some(Json.toJson(body).toString()), Some(""))
      val result: HttpResponse = connector.sendLetter(nino, body).futureValue
      result.status mustBe OK
      result.body mustBe ""
    }

    "return NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
      val body: NPSFMNRequest = mock[NPSFMNRequest]
      stubPost(url(nino), NOT_FOUND, Some(Json.toJson(body).toString()), Some(jsonNotFound))
      val result: HttpResponse = connector.sendLetter(nino, body).futureValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonNotFound
    }

    "return RESOURCE_NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
      val body: NPSFMNRequest = mock[NPSFMNRequest]
      stubPost(url(nino), NOT_FOUND, Some(Json.toJson(body).toString()), Some(jsonResourceNotFound))
      val result: HttpResponse = connector.sendLetter(nino, body).futureValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonResourceNotFound
    }

    "return INTERNAL_SERVER_ERROR (500) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
      val body: NPSFMNRequest = mock[NPSFMNRequest]
      stubPost(url(nino), INTERNAL_SERVER_ERROR, Some(Json.toJson(body).toString()), Some(jsonInternalServerError))
      val result: HttpResponse = connector.sendLetter(nino, body).futureValue
      result.status mustBe INTERNAL_SERVER_ERROR
      result.body mustBe jsonInternalServerError
    }
  }

}