package littleMonadDemo.ch08ReaderMonad

import scala.util.chaining._

import littleMonadDemo.libMyCats._
import littleMonadDemo.libCompute.LibComputeWithMyCats._

object ReaderApp extends util.App {

  val plus1: Int => Int          = _ + 1
  val doubled: Int => Int        = _ * 2
  val rPlus1: Reader[Int, Int]   = Reader(plus1)
  val rDoubled: Reader[Int, Int] = Reader(doubled)

  // description
  val rResult: Reader[Int, (Int, Int)] = for {
    i1 <- rPlus1
    i2 <- rDoubled
  } yield (i1, i2)

  // interpretation / execution
  rResult.run(10) pipe println

  compute(rPlus1, rDoubled).run(10) pipe println // (11, 20)

  // (r1 andThen r2).run(10) pipe println // 22
  // (r1 andThen f2).run(10) pipe println // 22
  // (f1 andThen f2)(10) pipe println     // 22
  // f2(f1(10)) pipe println              // 22

}
