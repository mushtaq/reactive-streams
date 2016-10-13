package rs.library

import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

object Connector {
  def coupling[T](bufferSize: Int = 0, overflowStrategy: OverflowStrategy = OverflowStrategy.dropHead)(implicit materializer: Materializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.asPublisher(false))(Keep.both).run()
    (actorRef, Source.fromPublisher(publisher))
  }
}
