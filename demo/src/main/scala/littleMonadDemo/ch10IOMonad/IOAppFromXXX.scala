package littleMonadDemo.ch10IOMonad

import scala.util.chaining._

import littleMonadDemo.libCompute.LibComputeWithMyCats._
import littleMonadDemo.libMyCats._
import scala.util.Try
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future

object IOAppFromXXX extends util.App {

  "\n----- IO.fromTry:" pipe println

  val tryy = Try {
    if (true)
      42
    else
      throw new RuntimeException("RuntimeException: bla bla")
  }
  IO.fromTry(tryy)
    .unsafeRunToTry()
    .pipe(println)

  "\n----- IO.fromEither:" pipe println

  val either = Try {
    if (true)
      42
    else
      throw new RuntimeException("RuntimeException: bla bla")
  }.toEither
  IO.fromEither(either)
    .unsafeRunToEither()
    .pipe(println)

  "\n----- IO.fromFuture:" pipe println

  implicit val ec: ExecutionContext = ExecutionContext.global

  def getFuture(): Future[Int] = Future {
    "STARTING async computation" pipe println
    Thread sleep 2000L
    "FINISHED async computation" pipe println
    42
  }

  IO.fromFuture(getFuture()).unsafeRun() pipe println

  // just to prevent the main thread from being terminated before the Future has finished executing.
  Thread sleep 1000L
}
