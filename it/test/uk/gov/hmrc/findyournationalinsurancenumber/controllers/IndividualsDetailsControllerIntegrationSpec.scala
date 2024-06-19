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

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

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
import util.{WiremockStub, WireMockHelper}

import scala.concurrent.{ExecutionContext, Future}

class IndividualsDetailsControllerIntegrationSpec
  extends WiremockStub
     with WireMockHelper
     with MockitoSugar
     with GuiceOneServerPerSuite
     with BeforeAndAfterEach {

  val nino = "SOME_NINO"
  val resolveMerge = "Y"
  private val baseUrl = s"http://localhost:$port"
  private val url = s"$baseUrl/find-your-national-insurance-number/individuals/details/NINO/$nino/$resolveMerge"

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

  "individuals details endpoint" should {
    "respond with 200 status" in {
      stubIndividualsDetails(nino, resolveMerge)

      when(mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
        any[Predicate],
        any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(fakeRetrievalResult)

      val response = await(wsClient.url(url).get())
      response.status mustBe OK

      response.json mustBe Json.parse(
        """{
          |  "nino": "SOME_NIN",
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
          |}
          |""".stripMargin)
    }

    "respond with 404 status" in {
      stubIndividualsDetailsFailure(nino, resolveMerge)

      when(mockAuthConnector.authorise[Option[CredentialRole] ~ Option[String]](
        any[Predicate],
        any[Retrieval[Option[CredentialRole] ~ Option[String]]])(any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(fakeRetrievalResult)

      val response = await(wsClient.url(url).get())
      response.status mustBe NOT_FOUND

      response.json mustBe Json.parse(
        """{}""".stripMargin)
    }
  }
}
