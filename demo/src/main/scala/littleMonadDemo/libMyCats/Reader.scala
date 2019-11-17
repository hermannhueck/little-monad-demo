package littleMonadDemo.libMyCats

case class Reader[P, A](run: P => A) {

  def pure(a: A): Reader[P, A] =
    Reader(_ => a)

  def map[B](f: A => B): Reader[P, B] =
    //Reader(p => f(run(p)))
    Reader(run andThen f)

  def flatMap[B](f: A => Reader[P, B]): Reader[P, B] =
    Reader(p => f(run(p)).run(p))
}

object Reader {

  implicit def readerMonad[P]: Monad[Reader[P, ?]] = new Monad[Reader[P, ?]] {

    def pure[A](a: A): Reader[P, A] =
      Reader(_ => a)

    def flatMap[A, B](fa: Reader[P, A])(f: A => Reader[P, B]): Reader[P, B] =
      Reader(p => f(fa.run(p)).run(p))
  }
}
