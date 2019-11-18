package littleMonadDemo.ch10IOMonad

import scala.util.chaining._

import littleMonadDemo.libCompute.LibComputeWithMyCats._
import littleMonadDemo.libMyCats._

object IOAppCompute extends util.App {

  val io1 = IO(() => 5)
  val io2 = IO(() => 27)

  val io3: IO[(Int, Int)] = for {
    i1 <- io1
    i2 <- io2
  } yield (i1, i2)
  io3.unsafeRun() pipe println

  compute(io1, io2).unsafeRun() pipe println
}
