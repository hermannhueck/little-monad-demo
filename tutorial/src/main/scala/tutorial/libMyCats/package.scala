package tutorial

package object libMyCats {

  type Id[A] = A

  /*
  // specific definition for Id
  //
  implicit class IdSyntax[A](fa: Id[A]) {

    def flatMap[B](f: A => Id[B]): Id[B] =
      Monad[Id].flatMap(fa)(f)

    def map[B](f: A => B): Id[B] =
      Monad[Id].map(fa)(f)
  }
   */

  implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

    def flatMap[B](f: A => F[B]): F[B] =
      Monad[F].flatMap(fa)(f)

    def map[B](f: A => B): F[B] =
      Monad[F].map(fa)(f)
  }
}
