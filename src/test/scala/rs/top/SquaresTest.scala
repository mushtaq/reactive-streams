package rs.top

import akka.NotUsed
import akka.stream.scaladsl.Source
import rs.Specification
import rs.utils.Config.executionContext
import rs.utils.Config.mat

import scala.concurrent.Await
import scala.concurrent.duration._

class SquaresTest extends Specification {

  val timeout = 30.seconds

  it("blocking") {
    val result = Squares.blocking(1 to 10)
    println("===============call returned================")
    println(result)
  }


  it("async") {
    val resultF = Squares.async(1 to 20)
    println("===============call returned================")
    resultF.onComplete(println)

    Await.ready(resultF, timeout)
  }


  it("nonBlocking") {
    val resultF = Squares.nonBlocking(1 to 20)
    println("===============call returned================")
    resultF.onComplete(println)

    Await.ready(resultF, timeout)
  }

  it("streaming") {
    val stream: Source[Int, NotUsed] = Squares.streaming(1 to 20)
    println("===============call returned================")
    val eventualDone = stream.runForeach(x => println(s"                                                   emitting $x"))

    Await.ready(eventualDone, timeout)
  }
}
