/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package connectors

import com.codahale.metrics.MetricRegistry

trait MetricsSupport {

  def measure[T](name: String, registry: MetricRegistry)(
      block:           => T
  ): T = {
    val t = registry.timer(s"$name.timer").time()

    val b = block

    t.stop()

    b
  }

  def count[T](name: String, label: String, registry: MetricRegistry): Unit =
    registry.meter(s"$name.$label").mark()
}
