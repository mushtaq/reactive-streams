package rs

import rs.library.RsSuite

class PullTest extends RsSuite {
  it("eager") {
    initializeAndFork(numbers.toList)
  }
  describe("lazy") {
    describe("perishable") {
      it("simple") {
        separator()
        val xs = numbers.iterator.map(initialized)
        forkIterator(xs, xs)
      }
      it("cached") {
        separator()
        val xs = numbers.iterator.map(initialized)
        val cachedXs = xs.toStream
        forkIterator(cachedXs.iterator, cachedXs.iterator)
      }
    }
    describe("reusable") {
      it("simple") {
        initializeAndFork(numbers.view)
      }
      it("cached") {
        separator()
        val xs = numbers.view.map(initialized)
        forkSeq(xs.toStream.view)
      }
      it("full-memoized") {
        initializeAndFork(numbers.toStream)
      }
    }
  }

  def initializeAndFork(xs: Seq[Int]) = {
    separator()
    val ys = xs.map(initialized)
    forkSeq(ys)
  }

  def forkSeq(xs: Seq[Int]) = {
    separator()
    xs.map(squaring).map(squared).foreach(ignore)
    separator()
    xs.map(doubling).map(doubled).foreach(ignore)
    separator()
  }

  def forkIterator(xs: Iterator[Int], ys: Iterator[Int]) = {
    separator()
    xs.map(squaring).map(squared).foreach(ignore)
    separator()
    ys.map(doubling).map(doubled).foreach(ignore)
    separator()
  }
}
