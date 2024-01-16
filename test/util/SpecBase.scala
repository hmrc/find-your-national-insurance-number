/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package util

import config.AppConfig
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait BaseSpec
  extends AnyWordSpec with GuiceOneAppPerSuite with Matchers with PatienceConfiguration with BeforeAndAfterEach
    with MockitoSugar with ScalaFutures with Injecting {
  this: Suite =>

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val configValues: Map[String, AnyVal] =
    Map(
      "metrics.enabled"  -> false,
      "auditing.enabled" -> false
    )
  protected def localGuiceApplicationBuilder(): GuiceApplicationBuilder =
    GuiceApplicationBuilder()
      .configure(configValues)

  override implicit lazy val app: Application = localGuiceApplicationBuilder().build()

  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]

  lazy val config: AppConfig = inject[AppConfig]

  override def beforeEach(): Unit = {
    super.beforeEach()
  }
}
