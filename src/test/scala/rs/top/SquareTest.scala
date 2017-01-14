package rs.top

import rs.Specification

import scala.concurrent.Await
import scala.concurrent.duration._

class SquareTest extends Specification {

  val timeout = 30.seconds

  it("blocking") {
    val result = Square.blocking(10)

    println("===============call returned================")

    result mustEqual 100
  }

  it("async") {

    val resultF = Square.async(10)

    println("===============call returned================")

    Await.result(resultF, 10.seconds) mustEqual 100
  }

  it("nonBlocking") {

    val resultF = Square.nonBlocking(10)

    println("===============call returned================")

    Await.result(resultF, 10.seconds) mustEqual 100
  }
}
