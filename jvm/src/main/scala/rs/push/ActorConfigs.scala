package rs.push

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContext

class ActorConfigs(_system: ActorSystem, _mat: Materializer, _ec: ExecutionContext) {
  implicit val system = _system
  implicit val mat    = _mat
  implicit val ec     = _ec
}

object ActorConfigs {
  def forName(name: String) = {
    implicit val actorSystem = ActorSystem(name)
    new ActorConfigs(actorSystem, ActorMaterializer(), actorSystem.dispatcher)
  }
}
