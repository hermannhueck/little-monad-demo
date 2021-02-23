package tutorial.examples09

import tutorial.libMyCats.*

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import scala.util.chaining.*
import util.*

object IOAppToXXX extends App {

  line().green pipe println

  val random = scala.util.Random

  def somePossiblyFailingComputation[A](value: A): A =
    if random.nextBoolean then
      value
    else
      throw new RuntimeException("RuntimeException: bla bla")

  val io = IO { () =>
    somePossiblyFailingComputation(42)
  }

  // interpretation / execution of the program
  // NOT referentially transparent

  def runComputationThrowingException(): Unit = {
    io.unsafeRun() // may throw an Exception
      .pipe(println)
  }

  def runComputationReturningTry(): Unit = {
    io.unsafeRunToTry()
      .fold(t => println(t.getMessage), v => println(v))
  }

  def runComputationReturningEither(): Unit = {
    io.unsafeRunToEither()
      .fold(t => println(t.getMessage), v => println(v))
  }

  def runComputationReturningFuture(): Unit = {

    given ExecutionContext = ExecutionContext.global

    io.unsafeRunToFuture
      .onComplete {
        case Failure(exception) => println(exception.getMessage)
        case Success(value)     => println(value)
      }
    Thread.sleep(500L)
  }

  // runComputationThrowingException()
  runComputationReturningTry()
  runComputationReturningEither()
  runComputationReturningFuture()

  line().green pipe println
}
