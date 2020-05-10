package tutorial.libMyCatsForExamples01To09

import tutorial.libMyCats._
import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.annotation._

final case class IO[A](unsafeRun: () => A):

  @infix def map[B](f: A => B): IO[B] =
    IO(() => f(unsafeRun()))

  @infix def flatMap[B](f: A => IO[B]): IO[B] =
    IO(() => f(unsafeRun()).unsafeRun())

  // different unsafeRunXXX methods turn our IO
  // into a classical Try, Either or Future

  def unsafeRunToTry(): Try[A] =
    Try(unsafeRun())

  def unsafeRunToEither(): Either[Throwable, A] =
    unsafeRunToTry().toEither

  def unsafeRunToFuture(using ExecutionContext): Future[A] =
    Future(unsafeRun())
end IO

object IO:

  given Monad[IO]:
    override def pure[A](a: A): IO[A] = IO pure a
    override def [A, B](io: IO[A]) flatMap (f: A => IO[B]): IO[B] =
      io flatMap f

  // pure is eager, a is passed by value
  // corresponds to Future.successful
  @infix def pure[A](a: A): IO[A] =
    IO(() => a)
      
  @infix def succeed[A](a: A): IO[A] =
    pure(a)

  // eval is lazy, thunk is passed by name
  // corresponds to Future.apply
  def eval[A](thunk: => A): IO[A] =
    IO(() => thunk)
      
  // same as pure but for errors; also eager
  // corresponds to Future.failed
  @infix def raiseError[A](exception: Throwable): IO[A] =
    IO(() => throw exception)

  @infix def fail[A](exception: Throwable): IO[A] =
    raiseError(exception)

  // different fromXXX methods turn a classical
  // Try, Either or Future into an IO

  @infix def fromTry[A](tryy: => Try[A]): IO[A] =
    eval(tryy.get)

  @infix def fromEither[A](either: => Either[Throwable, A]): IO[A] =
    eval(either.toTry.get)

  // by name param prevents the future from being constructed and run immediately
  @infix def fromFuture[A](future: => Future[A], timeout: Duration = Duration.Inf): IO[A] =
    eval(Await.result(future, timeout)) // !!! Blocking when executed !!!
    // !!! Blocking doesn't give us a valid impl of fromFuture !!!
end IO
