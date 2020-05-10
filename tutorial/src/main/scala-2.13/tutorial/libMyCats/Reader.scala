package tutorial.libMyCats

final case class Reader[P, A](run: P => A) {

  // alias for run
  // def provide(param: P): A = run(param)

  def map[B](f: A => B): Reader[P, B] =
    // Reader(p => f(run(p))) // is the same as:
    Reader(run andThen f)

  def flatMap[B](f: A => Reader[P, B]): Reader[P, B] =
    Reader(p => f(run(p)).run(p))

  def andThen[B](that: Reader[A, B]) =
    Reader(this.run andThen that.run)

  def andThen[B](that: A => B) =
    Reader(this.run andThen that.apply)

  def compose[B](that: Reader[B, P]) =
    Reader(this.run compose that.run)

  def compose[B](that: B => P) =
    Reader(this.run compose that.apply)
}

object Reader {

  implicit def readerMonad[P]: Monad[Reader[P, ?]] = new Monad[Reader[P, ?]] {
    def pure[A](a: A): Reader[P, A] =
      Reader.pure(a)
    def flatMap[A, B](fa: Reader[P, A])(f: A => Reader[P, B]): Reader[P, B] =
      fa flatMap f
  }

  def pure[P, A](a: A): Reader[P, A] =
    Reader(_ => a)
}
