/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.findyournationalinsurancenumber.controllers

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers.{await, defaultAwaitTimeout}

class IndividualsDetailsControllerIntegrationSpec
  extends AnyWordSpec
     with Matchers
     with ScalaFutures
     with IntegrationPatience
     with GuiceOneServerPerSuite {

  val nino             = "SOME_NINO"
  val resolveMerge     = "Y"
  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl  = s"http://localhost:$port"
  private val url      = s"$baseUrl/find-your-national-insurance-number/individuals/details/NINO/$nino/$resolveMerge"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure("metrics.enabled" -> false)
      .build()

  "individuals details endpoint" should {
    "respond with 200 status" in {
      val response = await(wsClient.url(url).get())
      response.status shouldBe OK
    }
  }
}
