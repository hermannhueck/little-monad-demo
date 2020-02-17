package tutorial.examples01To07

import scala.util.chaining._
import scala.language.implicitConversions
import util._

import tutorial.libMyCats._
// TODO: Function1 Monad should be found in implicit scope (companion object) without explicit import
import tutorial.libMyCats.Monad.{given _}

@main def Example07Function1Monad: Unit = {

  line().green pipe println

  val plus1: Int => Int  = (x => x + 1)
  val times2: Int => Int = (x => x * 2)

  val fn1: Function1[Int, (Int, Int)] =
    for
      i1 <- plus1
      i2 <- times2
    yield (i1, i2)

  fn1(10) pipe println

  compute(plus1, times2).apply(10) pipe println // (11, 20)

  // TODO: specified type conflicts with inferred type for ??? reason
  // val fn2: Int => (Int, Int) = compute(plus1, times2)
  val fn2 = compute(plus1, times2)
  fn2(10) pipe println // (11, 20)

  line().green pipe println
}
