/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package config

import com.google.inject.AbstractModule
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.play.bootstrap.auth.DefaultAuthConnector

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[AppConfig]).asEagerSingleton()
    bind(classOf[AuthConnector]).to(classOf[DefaultAuthConnector]).asEagerSingleton()
  }
}
