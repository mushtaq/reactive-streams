package rs

import rs.library.RsSuite

class EagerTest extends RsSuite {

  it("eager") {
    val xs = numbers.toList.map(read)
    separator()
    forkSeq(xs)
  }

  def forkSeq(xs: Seq[Int]) = {
    xs.map(square).take(5).foreach(ignore)
    separator()
    xs.map(double).take(10).foreach(ignore)
  }
}
