package littleMonadDemo.ch05Functions

import scala.util.chaining._

object FunctionsApp extends util.App {

  private def someComputation(): Int = {
    val result = {
      Thread sleep 1000L // simlate computation
      42
    }
    result
  }

  val f0Verbose: Function0[Int] = new Function0[Int] {
    override def apply(): Int = someComputation()
  }
  f0Verbose.apply() pipe println
  val f0 = () => someComputation()
  f0() pipe println

  val f1Verbose: Function1[Int, Int] = new Function1[Int, Int] {
    override def apply(x: Int): Int = x + 1
  }
  f1Verbose.apply(41) pipe println
  val f1 = (x: Int) => x + 1
  f1(41) pipe println

  val f2Verbose: Function2[Int, Int, Int] = new Function2[Int, Int, Int] {
    override def apply(x: Int, y: Int): Int = x + y
  }
  f2Verbose.apply(40, 2) pipe println
  val f2 = (x: Int, y: Int) => x + y
  f2(40, 2) pipe println

  println

  val f1a: Function1[Int, Int] = (x: Int) => x + 1
  val f1b: Int => Int          = x => x + 1
  val f1c: Int => Int          = _ + 1
  val f1d                      = (x: Int) => x + 1
  val f1e                      = (_: Int) + 1

  f1a(41) pipe println
  f1b(41) pipe println
  f1c(41) pipe println
  f1d(41) pipe println
  f1e(41) pipe println

  println

  val plus1: (Int => Int)  = _ + 1
  val square: (Int => Int) = x => x * x

  val plus1Squared1 = plus1 andThen square
  plus1Squared1(5) pipe println

  val plus1Squared2 = square compose plus1
  plus1Squared2(5) pipe println

  (square andThen plus1)(5) pipe println
  (plus1 compose square)(5) pipe println
}
