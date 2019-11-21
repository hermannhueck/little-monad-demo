package littleMonadDemo.ch09IOMonad

import scala.util.chaining._

import littleMonadDemo.libCompute.WithMyCats._
import littleMonadDemo.libMyCats._
import scala.util.Try
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future

object IOAppToXXX extends util.App {

  val random = scala.util.Random

  def somePossiblyFailingComputation[A](value: A): A =
    if (random.nextBoolean)
      value
    else
      throw new RuntimeException("RuntimeException: bla bla")

  val io = IO { () =>
    somePossiblyFailingComputation(42)
  }

  // interpretation / execution of the program
  // NOT referentially transparent

  def runHelloThrowingException(): Unit = {
    io
      .unsafeRun() // may throw an Exception
      .pipe(println)
  }

  def runHelloReturningTry(): Unit = {
    io
      .unsafeRunToTry()
      .fold(t => println(t.getMessage), v => println(v))
  }

  def runHelloReturningEither(): Unit = {
    io
      .unsafeRunToEither()
      .fold(t => println(t.getMessage), v => println(v))
  }

  def runHelloReturningFuture(): Unit = {

    implicit val ec: ExecutionContext = ExecutionContext.global

    io
      .unsafeRunToFuture
      .onComplete {
        case Failure(exception) => println(exception.getMessage)
        case Success(value)     => println(value)
      }
    Thread sleep 500L
  }

  // runHelloThrowingException()
  runHelloReturningTry()
  runHelloReturningEither()
  runHelloReturningFuture()
}
