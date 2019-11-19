package littleMonadDemo.ch10IOMonad

import scala.util.chaining._

import littleMonadDemo.libCompute.LibComputeWithMyCats._
import littleMonadDemo.libMyCats._
import scala.util.Try
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future

object IOAppConsole extends util.App {

  // description of the program
  // referentially transparent
  val hello: IO[String] = for {
    _    <- IO.eval("What's your name?  " pipe print)
    name <- IO.eval(scala.io.StdIn.readLine())
    _    <- IO.eval { s"Hello $name!\n" pipe println }
    tryy <- IO.eval {
             if (false)
               name
             else
               throw new RuntimeException("RuntimeException: bla bla")
           }
  } yield tryy

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
    Thread sleep 500L
  }

  // runHelloThrowingException()
  // runHelloReturningTry()
  runHelloReturningEither()
  // runHelloReturningFuture() // a termional program is not well suited to be run asynchronously.
}
