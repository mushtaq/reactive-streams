package rs.streams

import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, ClosedShape}
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

  val squaringFlow = Flow[Int].throttle(200.millis).map(square).take(10)
  val doublingFlow = Flow[Int].throttle(100.millis).map(double).take(5)
  val sink = Sink.ignore

  def fork(xs: Source[Int, Any]) = {
    val graph1 = xs.via(squaringFlow).toMat(sink)(Keep.right)
    val graph2 = xs.via(doublingFlow).toMat(sink)(Keep.right)
    val future1 = graph1.run()
    Thread.sleep(500)
    separator()
    val future2 = graph2.run()

    await(future1.flatMap(_ => future2))
  }

  def multicastFork(xs: Source[Int, Any]) = {
    val graph = GraphDSL.create(sink, sink)(Keep.both) { implicit b => (sink1, sink2) =>
      import GraphDSL.Implicits._

      val broadcast = b.add(Broadcast[Int](2))

      xs ~> broadcast.in
            broadcast.out(0) ~> squaringFlow ~> sink1
            broadcast.out(1) ~> doublingFlow ~> sink2

      ClosedShape
    }

    val runnableGraph = RunnableGraph.fromGraph(graph)
    val (future1, future2) = runnableGraph.run()

    await(future1.flatMap(_ => future2))
  }

  override protected def afterAll() = {
    system.terminate()
  }
}
