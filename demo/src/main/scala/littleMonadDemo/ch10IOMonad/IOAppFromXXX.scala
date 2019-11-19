package littleMonadDemo.ch10IOMonad

import scala.util.chaining._

import littleMonadDemo.libCompute.LibComputeWithMyCats._
import littleMonadDemo.libMyCats._
import scala.util.Try
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object IOAppFromXXX extends util.App {

  "\n----- IO.fromTry:" pipe println

  def someComputation(): Int =
    if (false)
      42
    else
      throw new RuntimeException("RuntimeException: bla bla")

  val tryy = Try(someComputation())
  IO.fromTry(tryy)
    .unsafeRunToTry()
    .pipe(println)
  "\n----- IO.fromEither:" pipe println

  val either = Try(someComputation()).toEither
  IO.fromEither(either)
    .unsafeRunToEither()
    .pipe(println)

  "\n----- IO.fromFuture:" pipe println

  implicit val ec: ExecutionContext = ExecutionContext.global

  def getFuture(): Future[Int] = Future {
    "STARTING async computation" pipe println
    val result = someComputation()
    Thread sleep 2000L
    "FINISHED async computation" pipe println
    result
  }

  val fut =
    IO.fromFuture(getFuture()).unsafeRunToFuture

  //Await.result(fut, Duration.Inf) pipe println
  fut.onComplete(tryy => tryy.fold(t => t.getMessage, v => v.toString) pipe println)

  // just to prevent the main thread from being terminated before the Future has finished executing.
  Thread sleep 1000L
}
