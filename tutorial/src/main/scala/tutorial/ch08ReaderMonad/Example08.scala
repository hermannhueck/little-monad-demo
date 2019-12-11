package tutorial.ch08ReaderMonad

import scala.util.chaining._

import tutorial.libMyCats._
import tutorial.libCompute.WithMyCats._

// uncomment for use with Cats instead of libMyCats
// import cats._
// import cats.data._
// import cats.implicits._
// import tutorial.libCompute.LibComputeWithCats._

object Example08 extends util.App {

  val plus1: Int => Int          = _ + 1
  val doubled: Int => Int        = _ * 2
  val rPlus1: Reader[Int, Int]   = Reader(plus1)
  val rDoubled: Reader[Int, Int] = Reader(doubled)

  // Reader as Monad

  // description
  val rResult: Reader[Int, (Int, Int)] = for {
    i1 <- rPlus1
    i2 <- rDoubled
  } yield (i1, i2)

  // interpretation / execution
  rResult.run(10) pipe println

  compute(rPlus1, rDoubled).run(10) pipe println // (11, 20)

  // compositon with Reader: compose and andThen

  println

  (rDoubled compose rPlus1).run(10) pipe println // 22
  (rDoubled compose plus1).run(10) pipe println  // 22
  (rPlus1 andThen rDoubled).run(10) pipe println // 22
  (rPlus1 andThen doubled).run(10) pipe println  // 22
}
