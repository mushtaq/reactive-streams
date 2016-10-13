package rs.library

import akka.stream.scaladsl.Source

import scala.concurrent.duration.FiniteDuration

object Sources {
  def ticks(duration: FiniteDuration) = Source.tick(duration, duration, ())
  def numbers = Source.fromIterator(() => Iterator.from(1))
}
