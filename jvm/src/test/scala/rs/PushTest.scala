package rs

import akka.stream.scaladsl.{Flow, Broadcast, FlowGraph, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.scalatest.BeforeAndAfterAll
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
  }


  def fork(xs: Source[Int, Any]) = {
    Thread.sleep(500)
    separator()
    val future1 = xs.throttle(200.millis).map(squaring).map(squared).take(10).runForeach(ignore)
    Thread.sleep(500)
    separator()
    val future2 = xs.map(doubling).take(10).runForeach(ignore)
    await(future1.flatMap(_ => future2))
  }

  override protected def afterAll() = {
    system.shutdown()
  }
}
