package rs.library

import org.scalatest.{FunSpec, MustMatchers}
import rs.library.SourceExtensions.RichSource
import rs.library.TExtensions.RichT

import scala.concurrent.duration.DurationInt

class RsSuite extends FunSpec with MustMatchers {
  def numbers = 1 to 10
  def numberStream = Sources.numbers.throttle(100.millis).map(initialized)
  
  def squaring(x: Int) = { println(s"--- squaring:$x"); x*x }
  def doubling(x: Int) = { println(s"*** doubling:$x"); x*2 }

  def initialized(x: Int) = x.log("^^^ initialized")
  def squared(x: Int) = x.log("+++ squared value")
  def doubled(x: Int) = x.log("!!! doubled value")

  def separator() = println("="*50)
  def ignore(x: Int) = ()
}
