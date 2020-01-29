package tutorial.libMyCats

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Monad[F[_]] {

  // intrinsic abstract methods for Monad

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  // other concrete methods implemented in terms of flatMap and pure

  final def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))

  final def flatten[A](fa: F[F[A]]): F[A] =
    flatMap(fa)(identity)

  // analogous to Function1#andThen
  final def andThenF[A, B, C](f: A => F[B])(g: B => F[C]): A => F[C] =
    a => flatMap(f(a))(g)
  final def kleisli[A, B, C](f: A => F[B])(g: B => F[C]): A => F[C] =
    andThenF(f)(g)
  final def >=>[A, B, C](f: A => F[B])(g: B => F[C]): A => F[C] =
    andThenF(f)(g)

  // analogous to Function1#compose
  final def composeF[A, B, C](g: B => F[C])(f: A => F[B]): A => F[C] =
    andThenF(f)(g)
  final def cokleisli[A, B, C](g: B => F[C])(f: A => F[B]): A => F[C] =
    composeF(g)(f)
  final def <=<[A, B, C](g: B => F[C])(f: A => F[B]): A => F[C] =
    composeF(g)(f)
}

object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

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

  /*
  // This is how the eitherMonad impl would look like
  // in Scala syntax without kind-projector
  // using existential types and type projection.
  // Let's forget that quickly.
  //
  implicit def eitherMonad[L]: Monad[({ type lambda[x] = Either[L, x] })#lambda] =
  new Monad[({ type lambda[x] = Either[L, x] })#lambda] {

    def pure[A](a: A): Either[L, A] =
      Right(a)

    def flatMap[A, B](fa: Either[L, A])(f: A => Either[L, B]): Either[L, B] =
      fa flatMap f
  }
   */

  implicit def function1Monad[P]: Monad[P => ?] = new Monad[P => ?] {

    def pure[A](a: A): P => A =
      _ => a

    def flatMap[A, B](fa: P => A)(f: A => P => B): P => B =
      p => f(fa(p))(p)
  }
}
