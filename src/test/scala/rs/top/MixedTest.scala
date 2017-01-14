package rs.top

import rs.Specification

import scala.concurrent.Await
import scala.concurrent.duration._
import rs.utils.Config.executionContext

class MixedTest extends Specification {

  val timeout = 30.seconds

  it("nonBlocking with async") {

    val resultNonBlockingF = Squares.nonBlocking(1 to 20)
    println("===============nonBlocking call returned================")
    resultNonBlockingF.onComplete(x => println("non blocking result", x))

    val resultAsyncF = Squares.async(1 to 20)
    println("===============async call returned================")
    resultAsyncF.onComplete(x => println("async result", x))

    Await.ready(resultNonBlockingF, timeout)
    Await.ready(resultAsyncF, timeout)
  }

}
