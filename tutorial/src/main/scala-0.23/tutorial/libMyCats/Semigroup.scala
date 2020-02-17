package tutorial.libMyCats

import scala.annotation.infix

  trait Semigroup[A]:

  // laws:
  // - associativity

  @infix def (lhs: A).combine(rhs: A): A
end Semigroup

object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = summon // summoner


  trait IntSemigroup extends Semigroup[Int]:
    @infix override def (lhs: Int).combine(rhs: Int): Int = lhs + rhs

  given as Semigroup[Int] with IntSemigroup


  trait StringSemigroup extends Semigroup[String]:
    @infix override def (lhs: String).combine(rhs: String): String = lhs ++ rhs

  given as Semigroup[String] with StringSemigroup


  trait BooleanSemigroup extends Semigroup[Boolean]:
    @infix override def (lhs: Boolean).combine(rhs: Boolean): Boolean = lhs && rhs

  given as Semigroup[Boolean] with BooleanSemigroup


  trait ListSemigroup[A] extends Semigroup[List[A]]:
    @infix override def (lhs: List[A]).combine(rhs: List[A]): List[A] = lhs ++ rhs

  given [A] as Semigroup[List[A]] with ListSemigroup[A]


  trait OptionSemigroup[A: Semigroup] extends Semigroup[Option[A]]:
    @infix override def (lhs: Option[A]).combine(rhs: Option[A]): Option[A] =
      (lhs, rhs) match
        case (None, None) => None
        case (Some(left), None) => Some(left)
        case (None, Some(right)) => Some(right)
        case (Some(left), Some(right)) => Some(left combine right)

  given [A: Semigroup] as Semigroup[Option[A]] with OptionSemigroup[A]

  trait MapSemigroup[K, V: Semigroup] extends Semigroup[Map[K, V]]:
    @infix override def (lhs: Map[K, V]).combine(rhs: Map[K, V]): Map[K, V] =
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

  given [K, V: Semigroup] as Semigroup[Map[K, V]] with MapSemigroup[K, V]


  trait Function1AndThenSemigroup[A] extends Semigroup[A => A]:
    @infix override def (f: A => A).combine(g: A => A): A => A =
      f andThen g

  given [A] as Semigroup[A => A] = new Function1AndThenSemigroup[A] {}


  class Function1ComposeSemigroup[A] extends Semigroup[A => A]:
    @infix override def (f: A => A).combine(g: A => A): A => A =
      f compose g

  def function1ComposeSemigroup[A]: Semigroup[A => A] = new Function1ComposeSemigroup[A]


  class Function1ApplySemigroup[A, B: Semigroup] extends Semigroup[A => B]:
    @infix override def (f: A => B).combine(g: A => B): A => B =
      a => f(a) combine g(a)

  def function1ApplySemigroup[A, B: Semigroup]: Semigroup[A => B] = new Function1ApplySemigroup[A, B]
}


//@infix def [A](lhs: A).combine(rhs: A)(given sg: Semigroup[A]): A =
  //sg.combine(lhs, rhs)
