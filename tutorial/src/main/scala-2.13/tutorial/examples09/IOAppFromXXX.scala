package tutorial.examples09

import tutorial.libMyCats._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.chaining._

object IOAppFromXXX extends util.App {

  "\n----- IO.fromTry:" pipe println

  val random = scala.util.Random

  def somePossiblyFailingComputation[A](value: A): A =
    if (random.nextBoolean)
      value
    else
      throw new RuntimeException("RuntimeException: bla bla")

  val tryy = Try(somePossiblyFailingComputation(42))
  val io1  = IO.fromTry(tryy)
  io1.unsafeRunToTry() pipe println
  "\n----- IO.fromEither:" pipe println

  val either = Try(somePossiblyFailingComputation(42)).toEither
  val io2    = IO.fromEither(either)
  io2.unsafeRunToEither() pipe println

  "\n----- IO.fromFuture:" pipe println

  implicit val ec: ExecutionContext = ExecutionContext.global

  def getFuture(): Future[Int] = Future {
    "STARTING async computation" pipe println
    val result = somePossiblyFailingComputation(42)
    Thread sleep 2000L
    "FINISHED async computation" pipe println
    result
  }

  val io3 = IO.fromFuture(getFuture())
  val fut = io3.unsafeRunToFuture

  //Await.result(fut, Duration.Inf) pipe println
  fut.onComplete(tryy => tryy.fold(t => t.getMessage, v => v.toString) pipe println)

  // just to prevent the main thread from being terminated before the Future has finished executing.
  Thread sleep 1000L
}
