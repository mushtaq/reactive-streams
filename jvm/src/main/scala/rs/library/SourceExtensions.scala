package rs.library

import akka.stream.Materializer
import akka.stream.scaladsl.{Zip, FlowGraph, Sink, Source}

import scala.concurrent.duration.FiniteDuration

object SourceExtensions {

  implicit class RichSource[Out, Mat](val source: Source[Out, Mat]) extends AnyVal {

    def zip[Out2, Mat2](other: Source[Out2, Mat2]) = Source(source) { implicit b => src =>
      import FlowGraph.Implicits._

      val zipper = b.add(Zip[Out, Out2]())

      src ~> zipper.in0
      other ~> zipper.in1

      zipper.out
    }

    def throttle(duration: FiniteDuration) = source.zip(Sources.ticks(duration)).map(_._1)

    def hot(implicit mat: Materializer) = {
      val (actorRef, hotSource) = Connector.coupling[Out]()
      source.runForeach(x => actorRef ! x)
      hotSource
    }

    def multicast(implicit mat: Materializer) = Source(source.runWith(Sink.fanoutPublisher(2, 2)))
  }

}
