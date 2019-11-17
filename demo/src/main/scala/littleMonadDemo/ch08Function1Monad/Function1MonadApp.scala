package littleMonadDemo.ch08Function1Monad

import scala.util.chaining._

import littleMonadDemo.libMyCats._
import littleMonadDemo.libCompute.LibComputeWithMyCats._

// uncomment for use with Cats instead of libMyCats
// import cats._
// import cats.data._
// import cats.implicits._
// import littleMonadDemo.libCompute.LibComputeWithCats._

object Function1MonadApp extends util.App {

  val f1: Int => Int = (x => x + 1)
  val f2: Int => Int = (x => x * 2)

  val f3: Function1[Int, (Int, Int)] = for {
    i1 <- f1
    i2 <- f2
  } yield (i1, i2)

  f3(10) pipe println

  compute(f1, f2).apply(10) pipe println // (11, 20)
  val f4 = compute(f1, f2)
  f4(10) pipe println // (11, 20)
}
