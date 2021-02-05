package tutorial.examples10To12

trait Joiner[A]:
  def zero: A
  extension (lhs: A)
    infix def join (rhs: A): A
  extension (as: Seq[A])
    infix def joinAll: A =
      as.fold(zero)(_ join _)

object Joiner:

  def apply[A: Joiner]: Joiner[A] = summon

  given Joiner[Int] with
    override def zero: Int = 0
    extension (lhs: Int)
      infix override def join(rhs: Int): Int =
        lhs + rhs

  given[A]: Joiner[List[A]] with
    override def zero: List[A] = List.empty[A]
    extension (lhs: List[A])
      infix override def join(rhs: List[A]): List[A] =
        lhs ++ rhs
