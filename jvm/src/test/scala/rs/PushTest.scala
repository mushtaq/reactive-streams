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
    it("multicast") {
      fork(numberStream.multicast)
    }
    it("multicast-single-trigger") {
      multicastFork(numberStream)
    }
  }

  val squaringFlow = Flow[Int].throttle(200.millis).map(squaring).map(squared)
  val doublingFlow = Flow[Int].map(doubling).map(doubled)
  val sink = Flow[Int].take(10).toMat(Sink.ignore)(Keep.right)

  def fork(xs: Source[Int, Any]) = {
    val graph1 = xs.via(squaringFlow).toMat(sink)(Keep.right)
    val graph2 = xs.via(doublingFlow).toMat(sink)(Keep.right)
    /*
        above is same as:
        val graph1 = FlowGraph.closed(sink) { implicit b => s =>
          import FlowGraph.Implicits._
          xs ~> squaringFlow ~> s
        }
    */
    val future1 = graph1.run()
    Thread.sleep(500)
    separator()
    val future2 = graph2.run()

    await(future1.flatMap(_ => future2))
  }

  def multicastFork(xs: Source[Int, Any]) = {
    val graph = FlowGraph.closed(sink, sink)(Keep.both) { implicit b => (sink1, sink2) =>
      import FlowGraph.Implicits._

      val broadcast = b.add(Broadcast[Int](2))

      xs ~> broadcast.in
            broadcast.out(0) ~> squaringFlow ~> sink1
            broadcast.out(1) ~> doublingFlow ~> sink2
    }

    val (future1, future2) = graph.run()

    await(future1.flatMap(_ => future2))
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
