package tutorial.libMyCats

trait Semigroup[A] {

  // laws:
  // - associativity

  def combine(lhs: A, rhs: A): A
}

object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = summon // summoner


  trait IntSemigroup extends Semigroup[Int]
    override def combine(lhs: Int, rhs: Int): Int = lhs + rhs

  given Semigroup[Int] with IntSemigroup


  trait BooleanSemigroup extends Semigroup[Boolean]
    override def combine(lhs: Boolean, rhs: Boolean): Boolean = lhs && rhs

  given Semigroup[Boolean] with BooleanSemigroup


  trait ListSemigroup[A] extends Semigroup[List[A]]
    override def combine(lhs: List[A], rhs: List[A]): List[A] = lhs ++ rhs

  given [A]: Semigroup[List[A]] with ListSemigroup[A]
}
