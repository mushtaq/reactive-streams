package rs.library

import akka.stream.scaladsl._

import scala.concurrent.duration.FiniteDuration

object FlowExtensions {
  implicit class RichFlow[In, Out, Mat](val flow: Flow[In, Out, Mat]) extends AnyVal {
    def throttle(duration: FiniteDuration) = flow.zip(Sources.ticks(duration)).map(_._1)
  }
}
