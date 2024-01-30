/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package util

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait WireMockHelper extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>

  val wireHost: String       = "localhost"
  val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())
  lazy val wirePort: Int     = server.port()

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  def overrideConfig: Map[String, Any] =
    Map(
      "external-url.individual-details.host" -> wireHost,
      "external-url.individual-details.port" -> wirePort,
      "auditing.enabled"                     -> false,
      "metrics.enabled"                      -> false
    )
}
