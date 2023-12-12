/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package models

import cats.data.EitherT
import cats.syntax.either._
import models.errors.IndividualDetailsError
import scala.concurrent.{ExecutionContext, Future}

object IndividualDetailsResponseEnvelope {
  type IndividualDetailsResponseEnvelope[T] = EitherT[Future, IndividualDetailsError, T]

  def apply[T](value: T): IndividualDetailsResponseEnvelope[T] =
    EitherT[Future, IndividualDetailsError, T](Future.successful(value.asRight[IndividualDetailsError]))

  def apply[T, E](value: Either[IndividualDetailsError, T]): IndividualDetailsResponseEnvelope[T] =
    EitherT(Future successful value)

  def fromEitherF[E <: IndividualDetailsError, T](value: Future[Either[E, T]]): IndividualDetailsResponseEnvelope[T] = EitherT(value)

  def fromError[E <: IndividualDetailsError, T](error: E): IndividualDetailsResponseEnvelope[T] = EitherT(Future.successful(error.asLeft[T]))

  def fromF[T](value: Future[T])(implicit ec: ExecutionContext): IndividualDetailsResponseEnvelope[T] = EitherT(value.map(_.asRight[IndividualDetailsError]))
}
