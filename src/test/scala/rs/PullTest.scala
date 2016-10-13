package rs

import rs.library.RsSuite

class PullTest extends RsSuite {

  describe("perishable") {
    it("simple") {
      val xs = numbers.iterator.map(read)
      forkIterator(xs, xs)
    }

    describe("memoized") {
      it("incremental") {
        val xs = numbers.iterator.map(read)
        val cachedXs = xs.toStream
        forkIterator(cachedXs.iterator, cachedXs.iterator)
      }
      it("minimal") {
        val xs = numbers.iterator.map(read)
        val (it1, it2) = xs.duplicate
        forkIterator(it1, it2)
      }
    }
  }

  describe("reusable") {
    it("simple") {
      val ys = numbers.view.map(read)
      forkSeq(ys)
    }

    describe("memoized") {
      it("incremental") {
        val xs = numbers.view.map(read)
        forkSeq(xs.toStream.view)
      }
      it("automatic") {
        val ys = numbers.toStream.map(read)
        forkSeq(ys)
      }
    }
  }

  def forkSeq(xs: Seq[Int]) = {
    xs.map(square).take(10).foreach(ignore)
    separator()
    xs.map(double).take(5).foreach(ignore)
  }

  def forkIterator(xs: Iterator[Int], ys: Iterator[Int]) = {
    xs.map(square).take(10).foreach(ignore)
    separator()
    ys.map(double).take(5).foreach(ignore)
  }
}
