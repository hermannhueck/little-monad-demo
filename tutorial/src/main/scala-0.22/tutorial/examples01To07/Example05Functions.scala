package tutorial.examples01To07

import scala.util.chaining._
import util._
import scala.language.implicitConversions

@main def Example05: Unit = {

  lineStart() pipe println

  def someComputation(): Int = {
    val result = {
      Thread.sleep(1000L) // simulate computation
      42
    }
    result
  }

  // Function0
  val f0Verbose: Function0[Int] = new Function0[Int] {
    override def apply(): Int = someComputation()
  }
  f0Verbose.apply() pipe println

  val f0: () => Int = () => someComputation()
  f0() pipe println

  // Function1
  val f1Verbose: Function1[Int, Int] = new Function1[Int, Int] {
    override def apply(x: Int): Int = x + 1
  }
  f1Verbose.apply(41) pipe println

  val f1: Int => Int = x => x + 1
  f1(41) pipe println

  // Function2
  val f2Verbose: Function2[Int, Int, Int] = new Function2[Int, Int, Int] {
    override def apply(x: Int, y: Int): Int = x + y
  }
  f2Verbose.apply(40, 2) pipe println

  val f2: (Int, Int) => Int = (x, y) => x + y
  f2(40, 2) pipe println

  println

  // different ways to define Function1

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

  // Function1 composition: 'andThen' and 'compose'

  val plus1: (Int => Int)  = _ + 1
  val square: (Int => Int) = x => x * x

  assert {
    (plus1 andThen square)(42) ==
      (square compose plus1)(42)
  }

  val plus1Squared1 = plus1 andThen square
  plus1Squared1(5) pipe println

  val plus1Squared2 = square compose plus1
  plus1Squared2(5) pipe println

  (square andThen plus1)(5) pipe println
  (plus1 compose square)(5) pipe println

  println

  // Tupling and Currying

  val add: Function2[Int, Int, Int] = _ + _

  val addTupled: Function1[(Int, Int), Int] = add.tupled

  val pair: (Int, Int)  = (21, 21)
  val sumFromTuple: Int = addTupled(pair) // 42
  sumFromTuple pipe println

  val addCurried: Int => (Int => Int) = add.curried

  val plus40    = addCurried(40)
  val sum1: Int = plus40(2) // 42
  sum1 pipe println

  val sum2: Int = plus40(3) // 43
  sum2 pipe println

  val sum3 = addCurried(40)(2)
  sum3 pipe println

  println

  // Currying Function4
  
  def add4(x1: Int, x2: Int, x3: Int, x4: Int): Int =
    x1 + x2 + x3 + x4

  val fAdd4: Function4[Int, Int, Int, Int, Int] = add4
  val fAdd4b: (Int, Int, Int, Int) => Int = add4
  val add4Curried: Int => Int => Int => Int => Int = fAdd4.curried
  val plus2: Int => Int => Int => Int = add4Curried(2)
  val plus4: Int => Int => Int = plus2(2)
  val plus6: Int => Int = plus4(2)
  val result: Int = plus6(2)
  result pipe println

  val sum4a = add4Curried(2)(2)(2)(2)
  sum4a pipe println

  lineEnd() pipe println
}
