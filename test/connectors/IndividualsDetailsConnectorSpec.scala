/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import config.AppConfig
import models._
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.test.{DefaultAwaitTimeout, Injecting}
import uk.gov.hmrc.http.HttpClient
import util.{WiremockStub, WireMockHelper}

import java.util.UUID

class IndividualsDetailsConnectorSpec
  extends WiremockStub
    with WireMockHelper
    with MockitoSugar
    with DefaultAwaitTimeout
    with Injecting {

  override implicit lazy val app: Application = app(
    Map("external-url.individual-details.port" -> server.port(),
    )
  )

  val nino = "nino"
  val resolveMerge = "test"

  val jsonInternalServerError = s"""
                |{
                |  "jsonServiceError": {
                |    "requestURL": "/individuals/details/NINO/$nino?resolveMerge=$resolveMerge",
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
                |    "requestURL": "/individuals/details/NINO/$nino?resolveMerge=$resolveMerge",
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
                |    "requestURL": "/individuals/details/NINO/$nino?resolveMerge=$resolveMerge",
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
      val httpClient = app.injector.instanceOf[HttpClient]
      val config = app.injector.instanceOf[AppConfig]
      new IndividualDetailsConnector(httpClient, config)
    }
  }

  "Individuals details Connector" must {

    trait LocalSetup extends SpecSetup {
      def url(nino: String) = s"/individuals/details/NINO/$nino?resolveMerge=$resolveMerge"
    }

    "return Ok (200) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      stubGet(url(nino), OK,  Some(""))
      val result = connector.getIndividualDetails(nino, resolveMerge).futureValue.leftSideValue
      result.status mustBe OK
      result.body mustBe ""
    }

    "return NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      stubGet(url(nino), NOT_FOUND, Some(jsonNotFound))
      val result = connector.getIndividualDetails(nino, resolveMerge).futureValue.leftSideValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonNotFound
    }

    "return RESOURCE_NOT_FOUND (404) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      stubGet(url(nino), NOT_FOUND, Some(jsonResourceNotFound))
      val result = connector.getIndividualDetails(nino, resolveMerge).futureValue.leftSideValue
      result.status mustBe NOT_FOUND
      result.body mustBe jsonResourceNotFound
    }

    "return INTERNAL_SERVER_ERROR (500) when called with an invalid nino" in new LocalSetup {
      implicit val correlationId = CorrelationId(UUID.randomUUID())
      stubGet(url(nino), INTERNAL_SERVER_ERROR, Some(jsonInternalServerError))
      val result = connector.getIndividualDetails(nino, resolveMerge).futureValue.leftSideValue
      result.status mustBe INTERNAL_SERVER_ERROR
      result.body mustBe jsonInternalServerError
    }
  }

}