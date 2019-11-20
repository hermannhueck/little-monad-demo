package littleMonadDemo.libMyCats

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Monad[F[_]] {

  // intrinsic abstract methods for Monad

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  // other concrete methods implemented in terms
  // of flatMap and pure

  // Haskell operator for flatMap
  def >>=[A, B](fa: F[A])(f: A => F[B]): F[B] =
    this.flatMap(fa)(f)

  final def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))

  final def flatten[A](fa: F[F[A]]): F[A] =
    flatMap(fa)(identity)
}

object Monad {

  // summoner
  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]]
  // def apply[F[_]](implicit m: Monad[F]): Monad[F] = m

  implicit val listMonad: Monad[List] = new Monad[List] {

    override def pure[A](a: A): List[A] =
      List(a)

    override def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] =
      list flatMap f
  }

  implicit val optionMonad: Monad[Option] = new Monad[Option] {

    override def pure[A](a: A): Option[A] =
      Option(a)

    override def flatMap[A, B](option: Option[A])(f: A => Option[B]): Option[B] =
      option flatMap f
  }

  implicit def futureMonad(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future] {

    override def pure[A](a: A): Future[A] =
      Future.successful(a)

    override def flatMap[A, B](future: Future[A])(f: A => Future[B]): Future[B] =
      future flatMap f
  }

  implicit val idMonad: Monad[Id] = new Monad[Id] {

    override def pure[A](a: A): Id[A] =
      a

    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      f(fa)
  }

  // needs kind-projector in build.sbt
  // fix left type param to a L
  implicit def eitherMonad[L]: Monad[Either[L, ?]] = new Monad[Either[L, ?]] {

    def pure[A](a: A): Either[L, A] =
      Right(a)

    def flatMap[A, B](fa: Either[L, A])(f: A => Either[L, B]): Either[L, B] =
      fa flatMap f
  }

  implicit def function1Monad[P]: Monad[P => ?] = new Monad[P => ?] {

    def pure[A](a: A): P => A =
      _ => a

    def flatMap[A, B](fa: P => A)(f: A => P => B): P => B =
      p => f(fa(p))(p)
  }
}
