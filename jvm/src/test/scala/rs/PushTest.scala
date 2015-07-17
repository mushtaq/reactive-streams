package rs

import akka.stream.scaladsl.Source
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
  implicit val am = ActorMaterializer(ActorMaterializerSettings(system).withInputBuffer(initialSize = 1, maxSize = 1))

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
    val f1 = xs.throttle(1.second).map(squaring).map(squared).take(10).runForeach(ignore)
    val f2 = xs.map(doubling).take(10).runForeach(ignore)
    await(f1.flatMap(_ => f2))
  }
  
  override protected def afterAll() = {
    system.shutdown()
  }
}
