package tutorial.examples10

trait Joiner[A] with
  def zero: A
  def join(lhs: A, rhs: A): A
  def joinAll(as: A*): A =
    as.fold(zero)(join)

object Joiner with

  def apply[A: Joiner]: Joiner[A] = summon

  given Joiner[Int] with
    override def zero: Int = 0
    override def join(lhs: Int, rhs: Int): Int =
      lhs + rhs

  given [A]: Joiner[List[A]] with
    override def zero: List[A] = List.empty[A]
    override def join(lhs: List[A], rhs: List[A]): List[A] =
      lhs ++ rhs

  @scala.annotation.infix
  def [A: Joiner](lhs: A) join (rhs: A): A =
    Joiner[A].join(lhs, rhs)

  def [A: Joiner](as: List[A]) joinAll: A =
    Joiner[A].joinAll(as: _*)
