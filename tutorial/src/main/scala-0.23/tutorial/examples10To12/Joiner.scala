package tutorial.examples10To12

import scala.annotation.infix

trait Joiner[A]:
  def zero: A
  @infix def (lhs: A) join (rhs: A): A
  def (as: Seq[A]) joinAll: A =
    as.fold(zero)(_ join _)

object Joiner:

  def apply[A: Joiner]: Joiner[A] = summon

  given Joiner[Int]:
    override def zero: Int = 0
    @infix override def (lhs: Int).join(rhs: Int): Int =
      lhs + rhs

  given [A] as Joiner[List[A]]:
    override def zero: List[A] = List.empty[A]
    @infix override def (lhs: List[A]).join(rhs: List[A]): List[A] =
      lhs ++ rhs
