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


  trait StringSemigroup extends Semigroup[String]
    override def combine(lhs: String, rhs: String): String = lhs ++ rhs

  given Semigroup[String] with StringSemigroup


  trait BooleanSemigroup extends Semigroup[Boolean]
    override def combine(lhs: Boolean, rhs: Boolean): Boolean = lhs && rhs

  given Semigroup[Boolean] with BooleanSemigroup


  trait ListSemigroup[A] extends Semigroup[List[A]]
    override def combine(lhs: List[A], rhs: List[A]): List[A] = lhs ++ rhs

  given [A]: Semigroup[List[A]] with ListSemigroup[A]


  trait OptionSemigroup[A: Semigroup] extends Semigroup[Option[A]]
    override def combine(lhs: Option[A], rhs: Option[A]): Option[A] =
      (lhs, rhs) match
        case (None, None) => None
        case (Some(left), None) => Some(left)
        case (None, Some(right)) => Some(right)
        case (Some(left), Some(right)) => Some(left combine right)

  given [A: Semigroup]: Semigroup[Option[A]] with OptionSemigroup[A]


  trait MapSemigroup[K, V: Semigroup] extends Semigroup[Map[K, V]]
    override def combine(lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
      // this impl deletes one value, if the same key is found in both maps
      // lhs ++ rhs
      val rMapWithCommonKeysCombined =
        rhs
          .map { (key, value) =>
            if !lhs.keys.toSet.contains(key)
              key -> value
            else
              key -> (lhs(key) combine value)
            }
      lhs ++ rMapWithCommonKeysCombined

  given [K, V: Semigroup]: Semigroup[Map[K, V]] with MapSemigroup[K, V]


  trait Function1AndThenSemigroup[A] extends Semigroup[A => A]
    override def combine(f: A => A, g: A => A): A => A =
      f andThen g

  given [A]: Semigroup[A => A] = new Function1AndThenSemigroup[A] {}


  class Function1ComposeSemigroup[A] extends Semigroup[A => A]
    override def combine(f: A => A, g: A => A): A => A =
      f compose g

  def function1ComposeSemigroup[A]: Semigroup[A => A] = new Function1ComposeSemigroup[A]


  class Function1ApplySemigroup[A, B: Semigroup] extends Semigroup[A => B]
    override def combine(f: A => B, g: A => B): A => B =
      a => f(a) combine g(a)

  def function1ApplySemigroup[A, B: Semigroup]: Semigroup[A => B] = new Function1ApplySemigroup[A, B]
}


@scala.annotation.infix
def [A: Semigroup](lhs: A) combine (rhs: A): A =
  Semigroup[A].combine(lhs, rhs)
