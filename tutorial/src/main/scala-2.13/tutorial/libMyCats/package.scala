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

  def compute(i1: Int, i2: Int): (Int, Int) =
    compute(i1: Id[Int], i2: Id[Int])

  def compute[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
    for {
      i1 <- fInt1
      i2 <- fInt2
    } yield (i1, i2)

  def compute2[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
    fInt1.flatMap { i1 =>
      fInt2.map { i2 =>
        (i1, i2)
      }
    }
}
