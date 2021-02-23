package tutorial.libMyCats

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Monad[F[_]]:

  def pure[A](a: A): F[A]  
  extension [A](fa: F[A])
    infix def flatMap[B](f: A => F[B]): F[B]

  extension [A](fa: F[A])
    infix def map[B](f: A => B): F[B] =
    // flatMap(fa)(a => pure(f(a)))
    flatMap(fa)(f andThen pure)

  extension [A](fa: F[F[A]])
    infix def flatten: F[A] =
      flatMap(fa)(identity)
end Monad

object Monad:

  def apply[F[_]: Monad]: Monad[F] = summon[Monad[F]]

  given Monad[List] with
    override def pure[A](a: A): List[A] = List(a)
    extension [A](list: List[A])
      infix override def flatMap[B](f: A => List[B]): List[B] =
        list flatMap f

  given Monad[Option] with
    override def pure[A](a: A): Option[A] = Some(a)
    extension [A](option: Option[A])
      infix override def flatMap[B](f: A => Option[B]): Option[B] =
        option flatMap f
  
  given (using ExecutionContext): Monad[Future] with
    override def pure[A](a: A): Future[A] = Future.successful(a)
    extension [A](future: Future[A])
      infix override def flatMap[B](f: A => Future[B]): Future[B] =
        future flatMap f

  given Monad[Id] with
    override def pure[A](a: A): Id[A] = a
    extension [A](fa: Id[A])
      infix override def flatMap[B](f: A => Id[B]): Id[B] =
        f(fa)
  
  // given[L]: Monad[[R] =>> Either[L, R]] with
  given[L]: Monad[Either[L, *]] with // requires -Ykind-projector
    override def pure[A](a: A): Either[L, A] = Right(a)
    extension [A](fa: Either[L, A])
      infix override def flatMap[B](f: A => Either[L, B]): Either[L, B] =
        fa flatMap f

  // given[P]: Monad[[R] =>> P => R] with
  given[P]: Monad[P => *] with
    override def pure[A](a: A): P => A = _ => a
    extension [A](fa: P => A)
      infix override def flatMap[B](f: A => P => B): P => B =
        p => f(fa(p))(p)
end Monad
