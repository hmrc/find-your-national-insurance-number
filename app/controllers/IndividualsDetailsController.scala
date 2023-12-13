/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package controllers

import cats.data.EitherT
import config.AppConfig
import connectors.{AdditionalLogInfo, IndividualDetailsConnector}
import models.IndividualDetailsResponseEnvelope.IndividualDetailsResponseEnvelope
import models.{CorrelationId, IndividualDetailsIdentifier, IndividualDetailsResponseEnvelope}
import models.individualdetails.{IndividualDetails, ResolveMerge}
import play.api.http.Status.OK
import play.api.i18n.MessagesApi
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.crypto.{Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.http.HeaderCarrier

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class IndividualsDetailsController  @Inject()(
                                               val messagesApi: MessagesApi,
                                               val controllerComponents: MessagesControllerComponents,
                                               val authConnector: AuthConnector,
                                                individualDetailsConnector: IndividualDetailsConnector
                                             ) (implicit ec: ExecutionContext, appConfig: AppConfig) {

  def getIndividualDetails(identifier: String, resolveMerge: Boolean
                          )(implicit ec: ExecutionContext,hc: HeaderCarrier, correlationId: CorrelationId
  ): IndividualDetailsResponseEnvelope[IndividualDetails] = {
    implicit val crypto: Encrypter with Decrypter = SymmetricCryptoFactory.aesCrypto(appConfig.cacheSecretKey)
    implicit val correlationId: CorrelationId = CorrelationId(UUID.randomUUID())
    IndividualDetailsResponseEnvelope.fromEitherF(individualDetailsConnector.getIndividualDetails(identifier, ResolveMerge('Y')).value)

  }


}
