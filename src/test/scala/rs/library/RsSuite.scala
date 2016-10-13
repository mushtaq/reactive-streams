package rs.library

import org.scalatest.{FunSpec, MustMatchers}
import rs.library.SourceExtensions.RichSource

import scala.concurrent.duration.DurationInt

class RsSuite extends FunSpec with MustMatchers {
  def numbers = 1 to 20
  def numberStream = Sources.numbers.throttle(50.millis).map(read)

  def read(x: Int) = { println(s"reading: $x"); x }
  def square(x: Int) = { println(s"                     squaring:$x"); x*x }
  def double(x: Int) = { println(s"                                             doubling:$x"); x+x }

  def separator() = println("="*50)
  def ignore(x: Int) = ()
}
