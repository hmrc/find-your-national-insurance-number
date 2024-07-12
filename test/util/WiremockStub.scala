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

package util

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait WiremockStub
    extends AnyWordSpec
    with GuiceOneAppPerSuite
    with Status
    with HeaderNames
    with MimeTypes
    with Matchers
    with ScalaFutures
    with IntegrationPatience {

  implicit val hc: HeaderCarrier    = HeaderCarrier()
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  val server: WireMockServer

  implicit def app(confStrings: Map[String, Any], overrides: GuiceableModule*): Application =
    new GuiceApplicationBuilder()
      .configure(confStrings)
      .overrides(overrides: _*)
      .build()

  def stubGet(url: String, responseStatus: Int, responseBody: Option[String]): StubMapping = server.stubFor {
    val baseResponse = aResponse().withStatus(responseStatus).withHeader(CONTENT_TYPE, JSON)
    val response     = responseBody.fold(baseResponse)(body => baseResponse.withBody(body))

    get(url).willReturn(response)
  }

  def stubPost(
    url: String,
    responseStatus: Int,
    requestBody: Option[String],
    responseBody: Option[String]
  ): StubMapping = server.stubFor {
    val baseResponse = aResponse().withStatus(responseStatus).withHeader(CONTENT_TYPE, JSON)
    val response     = responseBody.fold(baseResponse)(body => baseResponse.withBody(body))

    requestBody.fold(post(url).willReturn(response))(requestBody =>
      post(url).withRequestBody(equalToJson(requestBody)).willReturn(response)
    )
  }

  def stubWithFault(url: String, requestBody: Option[String], fault: Fault): StubMapping = server.stubFor {
    val response = aResponse().withFault(fault)

    requestBody.fold(any(urlEqualTo(url)).willReturn(response))(requestBody =>
      any(urlEqualTo(url)).withRequestBody(equalToJson(requestBody)).willReturn(response)
    )
  }

  def stubWithDelay(
    url: String,
    responseStatus: Int,
    requestBody: Option[String],
    responseBody: Option[String],
    delay: Int
  ): StubMapping = server.stubFor {
    val baseResponse = aResponse().withStatus(responseStatus).withHeader(CONTENT_TYPE, JSON).withFixedDelay(delay)
    val response     = responseBody.fold(baseResponse)(body => baseResponse.withBody(body))

    requestBody.fold(any(urlEqualTo(url)).willReturn(response))(requestBody =>
      any(urlEqualTo(url)).withRequestBody(equalToJson(requestBody)).willReturn(response)
    )
  }

  def verifyCorrelationIdHeader(requestPattern: RequestPatternBuilder): Unit =
    server.verify(
      requestPattern.withHeader(
        "Correlation-Id",
        matching("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
      )
    )

  def stubIndividualsDetails(nino: String, resolveMerge: String) = {
    val portion: Int = 8
    val ninoWithoutSuffix = nino.take(portion)
    server.stubFor(get(urlMatching(s"/individuals/details/NINO/$ninoWithoutSuffix\\?resolveMerge=$resolveMerge"))
      .willReturn(
        aResponse()
          .withStatus(OK)
          .withBody(
            s"""{
               |  "nino": "$ninoWithoutSuffix",
               |  "ninoSuffix": "B",
               |  "accountStatusType": 0,
               |  "sex": "M",
               |  "dateOfEntry": "1978-12-17",
               |  "dateOfBirth": "1975-02-10",
               |  "dateOfBirthStatus": 1,
               |  "dateOfDeath": "2018-08-09",
               |  "dateOfDeathStatus": 2,
               |  "dateOfRegistration": "1976-01-01",
               |  "registrationType": 8,
               |  "adultRegSerialNumber": "25673ASN",
               |  "cesaAgentIdentifier": "SDGH/4",
               |  "cesaAgentClientReference": "DIGBY JONES",
               |  "permanentTSuffixCaseIndicator": 0,
               |  "currOptimisticLock": 94,
               |  "liveCapacitorInd": 0,
               |  "liveAgentInd": 0,
               |  "ntTaxCodeInd": 0,
               |  "mergeStatus": 0,
               |  "marriageStatusType": 4,
               |  "crnIndicator": 0,
               |  "nameList": {},
               |  "addressList": {
               |    "address": [
               |      {
               |        "addressSequenceNumber": 2,
               |        "addressSource": 4,
               |        "countryCode": 1,
               |        "addressType": 1,
               |        "addressStatus": 0,
               |        "addressStartDate": "2003-04-30",
               |        "addressLastConfirmedDate": "2003-04-30",
               |        "vpaMail": 254,
               |        "deliveryInfo": "THROUGH THE LETTERBOX",
               |        "pafReference": "NO IDEA",
               |        "addressLine1": "88 TESTING ROAD",
               |        "addressLine2": "TESTTOWN",
               |        "addressLine3": "TESTREGION",
               |        "addressLine4": "TESTAREA",
               |        "addressLine5": "TESTSHIRE",
               |        "addressPostcode": "XX77 6YY"
               |      }
               |    ]
               |  },
               |  "indicators": {
               |    "manualCodingInd": 0,
               |    "manualCodingReason": 10,
               |    "manualCodingOther": "JUST BECAUSE",
               |    "manualCorrInd": 0,
               |    "manualCorrReason": "A MANUAL CODING REASON ... OR TWO",
               |    "additionalNotes": "SOME ADDITIONAL NOTES",
               |    "deceasedInd": 1,
               |    "s128Ind": 0,
               |    "noAllowInd": 0,
               |    "eeaCmnwthInd": 0,
               |    "noRepaymentInd": 0,
               |    "saLinkInd": 0,
               |    "noATSInd": 0,
               |    "taxEqualBenInd": 0,
               |    "p2ToAgentInd": 0,
               |    "digitallyExcludedInd": 0,
               |    "bankruptcyInd": 0,
               |    "bankruptcyFiledDate": "2018-04-05",
               |    "utr": "9384/38TEN",
               |    "audioOutputInd": 0,
               |    "welshOutputInd": 0,
               |    "largePrintOutputInd": 0,
               |    "brailleOutputInd": 0,
               |    "specialistBusinessArea": 16,
               |    "saStartYear": "1992/93",
               |    "saFinalYear": "2017",
               |    "digitalP2Ind": 1
               |  },
               |  "residencyList": {
               |    "residency": [
               |      {
               |        "residencySequenceNumber": 12345,
               |        "dateLeavingUK": "2018-07-22",
               |        "dateReturningUK": "2052-04-05",
               |        "residencyStatusFlag": 0
               |      }
               |    ]
               |  }
               |}""".stripMargin
          )
      )
    )
  }

  def stubIndividualsDetailsFailure(nino: String, resolveMerge: String) = {
    val portion: Int = 8
    val ninoWithoutSuffix = nino.take(portion)
    server.stubFor(get(urlMatching(s"/individuals/details/NINO/$ninoWithoutSuffix\\?resolveMerge=$resolveMerge"))
      .willReturn(
        aResponse()
          .withStatus(NOT_FOUND)
          .withBody(
            s"""{}""".stripMargin
          )
      )
    )
  }

  def stubNPS(nino: String, response: Int) = {
    server.stubFor(post(urlMatching(s"/nps/nps-json-service/nps/itmp/find-my-nino/api/v1/individual/$nino"))
      .willReturn(
        aResponse()
          .withStatus(response)
          .withBody(
            s"""{
               |}""".stripMargin
          )
      )
    )
  }

}
