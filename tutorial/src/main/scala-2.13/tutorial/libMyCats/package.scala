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
