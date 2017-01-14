package rs.top

import rs.utils.Scheduler

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random
import rs.utils.Config.executionContext

object Square {

  def delay: Int = Random.nextInt(5000)

  def square(a: Int): Int = a * a

  def blocking(a: Int): Int = {
    println(s"begin squaring $a")

    Thread.sleep(delay)

    val result = square(a)

    println(s"                         done squaring $a")

    result
  }

  def async(a: Int): Future[Int] = Future {

    blocking(a)
  }

  def nonBlocking(a: Int): Future[Int] = {
    println(s"begin squaring $a")

    Scheduler.asFuture(delay.millis) {

      val result = square(a)

      println(s"                       done squaring $a")

      result
    }
  }
}
