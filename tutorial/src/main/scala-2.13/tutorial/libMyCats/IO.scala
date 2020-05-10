package tutorial.libMyCats

import scala.util.Try
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.util.chaining._

final case class IO[A](unsafeRun: () => A) {

  def map[B](f: A => B): IO[B] =
    IO(() => f(unsafeRun()))

  def flatMap[B](f: A => IO[B]): IO[B] =
    // type checks, but wrong impl:
    // f(unsafeRun())
    IO(() => f(unsafeRun()).unsafeRun())

  // different unsafeRunXXX methods turn our IO
  // into a classical Try, Either or Future

  def unsafeRunToTry(): Try[A] =
    Try(unsafeRun())

  def unsafeRunToEither(): Either[Throwable, A] =
    unsafeRunToTry().toEither

  def unsafeRunToFuture(implicit ec: ExecutionContext): Future[A] =
    Future(unsafeRun())
}

object IO {

  implicit val ioMonad: Monad[IO] = new Monad[IO] {
    override def pure[A](a: A): IO[A] =
      IO.pure(a)
    override def flatMap[A, B](io: IO[A])(f: A => IO[B]): IO[B] =
      io flatMap f
  }

  // eval is lazy, thunk is passed by name
  // corresponds to Future.apply
  def eval[A](thunk: => A): IO[A] =
    IO(() => thunk)

  // pure is eager, a is passed by value
  // corresponds to Future.successful
  def pure[A](a: A): IO[A] =
    IO(() => a)

  def succeed[A](a: A): IO[A] =
    pure(a)

  // same as pure but for errors; also eager
  // corresponds to Future.failed
  def raiseError[A](exception: Throwable): IO[A] =
    IO(() => throw exception)

  def fail[A](exception: Throwable): IO[A] =
    raiseError(exception)

  // different fromXXX methods turn a classical
  // Try, Either or Future into an IO

  def fromTry[A](tryy: Try[A]): IO[A] =
    IO(() => tryy.get)

  def fromEither[A](either: Either[Throwable, A]): IO[A] =
    IO(() => either.toTry.get)

  // by name param prevents the future from being constructed and run immediately
  def fromFuture[A](future: => Future[A], timeout: Duration = Duration.Inf): IO[A] =
    IO(() => Await.result(future, timeout)) // !!! Blocking when executed !!!
}
