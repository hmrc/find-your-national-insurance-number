/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package util

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object Stubs {

  private val FMNRetrievals: String =
    """
      |{
      |	"authorise": [{
      |		"authProviders": ["GovernmentGateway"]
      |	}],
      |	"retrieve": ["nino", "credentialRole", "internalId", "confidenceLevel","affinityGroup", "allEnrolments", "optionalName" ]
      |}
      |""".stripMargin

  def userLoggedInFMNUser(testUserJson: String): StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(FMNRetrievals))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(testUserJson)
        )
    )

  def userLoggedInIsNotFMNUser(error: String): StubMapping =
    stubFor(
      post(urlEqualTo("/auth/authorise"))
        .withRequestBody(equalToJson(FMNRetrievals))
        .willReturn(
          unauthorized.withHeader("WWW-Authenticate", s"""MDTP detail="$error"""")
        )
    )
}
