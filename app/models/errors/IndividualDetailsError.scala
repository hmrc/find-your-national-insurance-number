/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models.errors

import models.IndividualDetailsIdentifier

import scala.util.control.NoStackTrace

sealed abstract class IndividualDetailsError(message: String) extends Throwable {
  val errorMessage: String = message
}

final case class ConnectorError(statusCode: Int, message: String) extends IndividualDetailsError(message)

final case class InvalidIdentifier(identifier: IndividualDetailsIdentifier)
    extends IndividualDetailsError(s"Invalid identifier: $identifier")

final case object CacheNotFound extends IndividualDetailsError("cache not found")


final case object LockError extends IndividualDetailsError("Could not acquire lock")


final case class DataLockedException(sdesCorrelationId: String)
    extends IndividualDetailsError(s"Item with sdesCorrelationId $sdesCorrelationId was locked")

final case object PDFGenerationError extends IndividualDetailsError("Failed to generate pdf")

final case class SdesResponseException(status: Int, body: String)
    extends IndividualDetailsError(s"Unexpected response from SDES, status: $status, body: $body")
    with NoStackTrace
