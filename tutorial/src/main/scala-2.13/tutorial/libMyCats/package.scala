package tutorial

package object libMyCats {

  type Id[A] = A

  implicit final class SemigroupSyntax[A](private val lhs: A) extends AnyVal {
    @inline def combine(rhs: A)(implicit sg: Semigroup[A]): A =
      Semigroup[A].combine(lhs, rhs)
  }
    
  implicit final class MonoidSyntax[A](private val as: Seq[A]) extends AnyVal {
    @inline def combineAll(implicit m: Monoid[A]): A =
      Monoid[A].combineAll(as)
  }

  implicit final class MonadSyntax[F[_]: Monad, A](private val fa: F[A]) {
    @inline def flatMap[B](f: A => F[B]): F[B] =
      Monad[F].flatMap(fa)(f)
    @inline def map[B](f: A => B): F[B] =
      Monad[F].map(fa)(f)
  }

  implicit final class KleisliSyntaxOnFunction1[F[_]: Monad, A, B](f: A => F[B]) {
    @inline def andThenF[C](g: B => F[C]): A => F[C] = Monad[F].andThenF(f)(g)
    @inline def kleisli[C](g: B => F[C]): A => F[C] = Monad[F].kleisli(f)(g)
    @inline def >=>[C](g: B => F[C]): A => F[C] = Monad[F].>=>(f)(g)
  }
  implicit final class CoKleisliSyntaxOnFunction1[F[_]: Monad, B, C](g: B => F[C]) {
    @inline def composeF[A](f: A => F[B]): A => F[C] = Monad[F].composeF(g)(f)
    @inline def cokleisli[A](f: A => F[B]): A => F[C] = Monad[F].cokleisli(g)(f)
    @inline def <=<[A](f: A => F[B]): A => F[C] = Monad[F].<=<(g)(f)
  }

  def compute[A, B](a: A, b: B): (A, B) =
    compute(a: Id[A], b: Id[B])

  def compute[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    for {
      a <- fa
      b <- fb
    } yield (a, b)

  def compute2[F[_], A, B](fa: F[A], fb: F[B])(implicit m: Monad[F]): F[(A, B)] =
    fa.flatMap { a =>
      fb.map { b =>
        (a, b)
      }
    }

  def computeInts[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
    for {
      i1 <- fInt1
      i2 <- fInt2
    } yield (i1, i2)
}
