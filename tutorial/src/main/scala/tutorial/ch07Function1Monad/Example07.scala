package tutorial.ch07Function1Monad

import scala.util.chaining._

import tutorial.libMyCats._
import tutorial.libCompute.WithMyCats._

// uncomment for use with Cats instead of libMyCats
// import cats._
// import cats.data._
// import cats.implicits._
// import tutorial.libCompute.WithCats._

object Example07 extends util.App {

  val plus1: Int => Int  = (x => x + 1)
  val times2: Int => Int = (x => x * 2)

  val fn1: Function1[Int, (Int, Int)] = for {
    i1 <- plus1
    i2 <- times2
  } yield (i1, i2)

  fn1(10) pipe println

  compute(plus1, times2).apply(10) pipe println // (11, 20)

  val fn2: Int => (Int, Int) = compute(plus1, times2)
  fn2(10) pipe println // (11, 20)
}
