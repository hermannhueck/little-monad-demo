package tutorial.examples09

import tutorial.libMyCats.*

import scala.concurrent.{ExecutionContext, Await, Future}
import scala.concurrent.duration.*
import scala.util.Try

import scala.util.chaining.*
import util.*

object IOAppFromXXX extends App {

  line().green pipe println

  val random = scala.util.Random

  def somePossiblyFailingComputation[A](value: A): A =
    if random.nextBoolean then
      value
    else
      throw new RuntimeException("RuntimeException: bla bla")

  "\n----- IO.fromTry:" pipe println
  def tryy = Try(somePossiblyFailingComputation(42))
  val io1  = IO.fromTry(tryy)
  io1.unsafeRunToTry() pipe println

  "\n----- IO.fromEither:" pipe println
  def either = Try(somePossiblyFailingComputation(42)).toEither
  val io2    = IO.fromEither(either)
  io2.unsafeRunToEither() pipe println

  "\n----- IO.fromFuture:" pipe println
  given ec: ExecutionContext = ExecutionContext.global

  def getFuture(): Future[Int] = Future {
    "STARTING async computation" pipe println
    val result: Int = somePossiblyFailingComputation(42)
    Thread.sleep(1000L)
    "FINISHED async computation" pipe println
    result
  }

  val io3 = IO.fromFuture(getFuture(), 2.second)
  // io3.unsafeRunToEither() pipe println
  
  val fut = io3.unsafeRunToFuture(using ec)

  // Await.result(fut, Duration.Inf) pipe println
  fut.onComplete(tryy => tryy.fold(t => t.getMessage, v => v.toString) pipe println)

  // just to prevent the main thread from being terminated before the Future has finished executing.
  Thread.sleep(3000L)

  line().green pipe println
}
