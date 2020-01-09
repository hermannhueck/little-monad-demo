package tutorial.libMyCats

trait Semigroup[A] {

  // laws:
  // - associativity

  def combine(lhs: A, rhs: A): A
}

object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = implicitly // summoner


  trait IntSemigroup extends Semigroup[Int] {
    override def combine(lhs: Int, rhs: Int): Int = lhs + rhs
  }
  implicit val intSemigroup: Semigroup[Int] = new IntSemigroup {}


  trait StringSemigroup extends Semigroup[String] {
    override def combine(lhs: String, rhs: String): String = lhs ++ rhs
  }
  implicit val stringSemigroup: Semigroup[String] = new StringSemigroup {}


  trait BooleanSemigroup extends Semigroup[Boolean] {
    override def combine(lhs: Boolean, rhs: Boolean): Boolean = lhs && rhs
  }
  implicit val booleanSemigroup: Semigroup[Boolean] = new BooleanSemigroup {}


  class ListSemigroup[A] extends Semigroup[List[A]] {
    override def combine(lhs: List[A], rhs: List[A]): List[A] = lhs ++ rhs
  }
  implicit def listSemigroup[A]: Semigroup[List[A]] = new ListSemigroup[A]


  class OptionSemigroup[A: Semigroup] extends Semigroup[Option[A]] {
    override def combine(lhs: Option[A], rhs: Option[A]): Option[A] =
      (lhs, rhs) match {
        case (Some(la), Some(ra)) => Some(la combine ra)
        case (Some(la), None) => Some(la)
        case (None, Some(ra)) => Some(ra)
        case (None, None) => None
      }
  }
  implicit def optionSemigroup[A: Semigroup]: Semigroup[Option[A]] = new OptionSemigroup[A]


  class MapSemigroup[K, V: Semigroup] extends Semigroup[Map[K, V]] {
    override def combine(lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
      if (false)
        mapCombine1(lhs, rhs)
      else
        mapCombine2(lhs, rhs)
  }

  // this impl deletes one value, if the same key is found in both maps
  private def mapCombine1[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs ++ rhs

  private def mapCombine2[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] = {
      val rMapWithCommonKeysCombined =
        rhs
          .map { case (key, value) =>
            if (!lhs.keys.toSet.contains(key))
              key -> value
            else
              key -> (value combine lhs(key))
            }
      lhs ++ rMapWithCommonKeysCombined
  }

  implicit def mapSemigroup[K, V: Semigroup]: Semigroup[Map[K, V]] = new MapSemigroup[K, V]


  class Function1AndThenSemigroup[A] extends Semigroup[A => A] {
    override def combine(f: A => A, g: A => A): A => A =
      f andThen g
  }

  implicit def function1AndThenSemigroup[A]: Semigroup[A => A] = new Function1AndThenSemigroup[A]


  class Function1ComposeSemigroup[A] extends Semigroup[A => A] {
    override def combine(f: A => A, g: A => A): A => A =
      f compose g
  }

  def function1ComposeSemigroup[A]: Semigroup[A => A] = new Function1ComposeSemigroup[A]

  
  class Function1ApplySemigroup[A, B: Semigroup] extends Semigroup[A => B] {
    override def combine(f: A => B, g: A => B): A => B =
      a => f(a) combine g(a)
  }

  def function1ApplySemigroup[A, B: Semigroup]: Semigroup[A => B] = new Function1ApplySemigroup[A, B]
}
