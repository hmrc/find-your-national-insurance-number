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

package config

import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration

class AppConfigSpec extends PlaySpec with MockitoSugar {

  "AppConfig" should {

    "return DesApiServiceConfig for individualDetails" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)

      val configMap = Map(
        "auth-token" -> "testToken",
        "environment" -> "testEnvironment",
        "originator-id" -> "testOriginatorId"
      )
      val configuration = Configuration.from(configMap)

      when(mockConfig.get[Configuration]("microservice.services.individual-details")).thenReturn(configuration)

      val result = appConfig.individualDetails

      result.token mustBe "testToken"
      result.environment mustBe "testEnvironment"
      result.originatorId mustBe "testOriginatorId"
    }
  }
}
