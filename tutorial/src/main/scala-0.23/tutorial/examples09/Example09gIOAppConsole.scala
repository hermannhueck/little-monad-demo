package tutorial.examples09

import tutorial.libMyCats._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

import scala.util.chaining._
import scala.language.implicitConversions
import util._

object IOAppConsole extends App {

  line().green pipe println

  val random = scala.util.Random

  def somePossiblyFailingComputation[A](value: A): A =
    if random.nextBoolean
      value
    else
      throw new RuntimeException("RuntimeException: bla bla")

  // description of the program
  // referentially transparent
  val hello: IO[String] =
    for
      _     <- IO.eval("What's your name?  " pipe print)
      name  <- IO.eval(scala.io.StdIn.readLine())
      _     <- IO.eval { s"Hello $name!\n" pipe println }
      name2 <- IO.eval { somePossiblyFailingComputation(name) }
    yield name2

  // interpretation / execution of the program
  // NOT referentially transparent

  def runHelloThrowingException(): Unit = {
    hello
      .unsafeRun() // may throw an Exception
      .pipe(println)
  }

  def runHelloReturningTry(): Unit = {
    hello
      .unsafeRunToTry()
      .fold(t => println(t.getMessage), name => println(name))
  }

  def runHelloReturningEither(): Unit = {
    hello
      .unsafeRunToEither()
      .fold(t => println(t.getMessage), name => println(name))
  }

  def runHelloReturningFuture(): Unit = {

    implicit val ec: ExecutionContext = ExecutionContext.global

    hello
      .unsafeRunToFuture
      .onComplete {
        case Failure(exception) => println(exception.getMessage)
        case Success(value)     => println(value)
      }
    Thread.sleep(500L)
  }

  // runHelloThrowingException()
  // runHelloReturningTry()
  runHelloReturningEither()
  // runHelloReturningFuture() // a termional program is not well suited to be run asynchronously.

  line().green pipe println
}
