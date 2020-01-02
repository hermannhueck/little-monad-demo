package tutorial.libMyCatsUntilExample09

import scala.annotation.infix

final case class Reader[P, A](run: P => A) {

  // alias for run
  @infix def provide(param: P): A = run(param)

  @infix def map[B](f: A => B): Reader[P, B] =
    // Reader(p => f(run(p))) // is the same as:
    Reader(run andThen f)

  @infix def flatMap[B](f: A => Reader[P, B]): Reader[P, B] =
    Reader(p => f(run(p)).run(p))

  @infix def andThen[B](that: Reader[A, B]): Reader[P, B] =
    Reader(this.run andThen that.run)

  @infix def andThen[B](that: A => B): Reader[P, B] =
    Reader(this.run andThen that.apply)

  @infix def compose[B](that: Reader[B, P]): Reader[B, A] =
    Reader(this.run compose that.run)

  @infix def compose[B](that: B => P): Reader[B, A] =
    Reader(this.run compose that.apply)
}

object Reader {

  given [P]: Monad[[R] =>> Reader[P, R]]
    override def pure[A](a: A): Reader[P, A] = Reader pure a
    override def [A, B](fa: Reader[P, A]) flatMap (f: A => Reader[P, B]): Reader[P, B] =
      fa flatMap f

  @infix def pure[P, A](a: A): Reader[P, A] =
    Reader(_ => a)
}
