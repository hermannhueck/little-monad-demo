package tutorial.libMyCats

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Monad[F[?]] {

  def pure[A](a: A): F[A]  
  def [A, B](fa: F[A]) flatMap (f: A => F[B]): F[B]

  def [A, B] (fa: F[A]) map (f: A => B): F[B] =
    // flatMap(fa)(a => pure(f(a)))
    flatMap(fa)(f andThen pure)

  def [A](fa: F[F[A]]) flatten: F[A] =
    flatMap(fa)(identity)
}

object Monad {

  def apply[F[_]: Monad]: Monad[F] = summon[Monad[F]]

  given Monad[List]
    override def pure[A](a: A): List[A] = List(a)
    override def [A, B](list: List[A]) flatMap (f: A => List[B]): List[B] =
      list flatMap f

  given Monad[Option]
    override def pure[A](a: A): Option[A] = Some(a)
    override def [A, B](option: Option[A]) flatMap (f: A => Option[B]): Option[B] =
      option flatMap f
  
  given (given ExecutionContext): Monad[Future]
    override def pure[A](a: A): Future[A] = Future.successful(a)
    override def [A, B](future: Future[A]) flatMap (f: A => Future[B]): Future[B] =
      future flatMap f

  given Monad[Id]
    override def pure[A](a: A): Id[A] = a
    override def [A, B](fa: Id[A]) flatMap (f: A => Id[B]): Id[B] =
      f(fa)
  
  given [L]: Monad[[R] =>> Either[L, R]]
    override def pure[A](a: A): Either[L, A] = Right(a)
    override def [A, B](fa: Either[L, A]) flatMap(f: A => Either[L, B]): Either[L, B] =
      fa flatMap f

  /*
  given [L]: Monad[Either[L, *]]
    override def pure[A](a: A): Either[L, A] = Right(a)
    override def [A, B](fa: Either[L, A]) flatMap(f: A => Either[L, B]): Either[L, B] =
      fa flatMap f
  */

  given [P]: Monad[[R] =>> P => R]
    override def pure[A](a: A): P => A = _ => a
    override def [A, B](fa: P => A) flatMap (f: A => P => B): P => B =
      p => f(fa(p))(p)
}
