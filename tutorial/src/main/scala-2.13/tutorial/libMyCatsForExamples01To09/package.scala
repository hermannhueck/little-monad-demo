package tutorial

package object libMyCatsForExamples01To09 {

  type Id[A] = A

  implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

    def flatMap[B](f: A => F[B]): F[B] =
      Monad[F].flatMap(fa)(f)

    def map[B](f: A => B): F[B] =
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
