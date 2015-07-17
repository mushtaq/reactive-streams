package rs

import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.scalatest.BeforeAndAfterAll
import rs.library.FlowExtensions.RichFlow
import rs.library.RsSuite
import rs.library.SourceExtensions.RichSource
import rs.library.Utils._
import rs.push.ActorConfigs

import scala.concurrent.duration.DurationInt

class PushTest extends RsSuite with BeforeAndAfterAll {

  val actorConfigs = ActorConfigs.forName("test")
  import actorConfigs._
  implicit val mat2 = ActorMaterializer(
    ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 1)
  )

  describe("hot") {
    it("unicast-error") {
      fork(numberStream.hot)
    }
    it("multicast") {
      fork(numberStream.hot.multicast)
    }
  }

  describe("cold") {
    it("unicast") {
      fork(numberStream)
    }
    it("fanout") {
      fork(numberStream.multicast)
    }
    it("multicast") {
      multicastFork(numberStream)
    }
  }

  val sink = Flow[Int].take(10).toMat(Sink.ignore)(Keep.right)
  val squaringFlow = Flow[Int].throttle(200.millis).map(squaring).map(squared)
  val doublingFlow = Flow[Int].map(doubling).map(doubled)

  def fork(xs: Source[Int, Any]) = {
    def branch(flow: Flow[Int, Int, Any]) = xs.via(flow).take(10).runWith(Sink.ignore)

    Thread.sleep(500)
    separator()
    val future1 = branch(squaringFlow)

    Thread.sleep(500)
    separator()
    val future2 = branch(doublingFlow)

    await(future1.flatMap(_ => future2))
  }

  def multicastFork(xs: Source[Int, Any]) = {
    val (future1, future2) = FlowGraph.closed(sink, sink)(Keep.both) { implicit b => (sink1, sink2) =>
      import FlowGraph.Implicits._

      val broadcast = b.add(Broadcast[Int](2))
      xs ~> broadcast.in

      separator()
      Thread.sleep(500)
      broadcast.out(0) ~> squaringFlow ~> sink1

      separator()
      Thread.sleep(500)
      broadcast.out(1) ~> doublingFlow ~> sink2
    }.run()

    await(future1.flatMap(_ => future2))
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
