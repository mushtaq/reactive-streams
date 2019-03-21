package rs.library

import akka.NotUsed
import akka.actor.ActorRef
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

object Connector {
  def coupling[T](bufferSize: Int = 0, overflowStrategy: OverflowStrategy = OverflowStrategy.dropHead)(implicit materializer: Materializer): (ActorRef, Source[T, NotUsed]) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.asPublisher(false))(Keep.both).run()
    (actorRef, Source.fromPublisher(publisher))
  }
}
