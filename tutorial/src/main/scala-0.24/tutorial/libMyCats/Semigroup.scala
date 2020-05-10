package tutorial.libMyCats

import scala.annotation.infix

trait Semigroup[A]:
  @infix def (lhs: A).combine(rhs: A): A
  // laws:
  // - associativity


object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = summon // summoner


  def instance[A](f: (A, A) => A): Semigroup[A] = new Semigroup[A] {
    @infix override def (lhs: A).combine(rhs: A): A = f(lhs, rhs)
  }

  given as Semigroup[Int] = instance(_ + _)

  given as Semigroup[String] = instance(_ ++ _)

  given as Semigroup[Boolean] = instance(_ && _)

  // conflict with Function1 Semigroup on line 52
  // given [A] as Semigroup[List[A]] = instance[List[A]](_ ++ _)
  given [A] as Semigroup[List[A]]:
    @infix override def (lhs: List[A]).combine(rhs: List[A]): List[A] = lhs ++ rhs

  def combineOptions[A: Semigroup]: (Option[A], Option[A]) => Option[A] =
    case (Some(la), Some(ra)) => Some(la combine ra)
    case (Some(la), None)     => Some(la)
    case (None, Some(ra))     => Some(ra)
    case (None, None)         => None

  given [A: Semigroup] as Semigroup[Option[A]] = instance(combineOptions)

  def combineMaps[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs ++ rhs
        .map {
          case key -> value if !lhs.keys.toSet.contains(key) =>
              key -> value
          case key -> value =>
              key -> (value combine lhs(key))
        }

  given [K, V: Semigroup] as Semigroup[Map[K, V]] = instance(combineMaps)

  given [A] as Semigroup[A => A] =  instance(_ andThen _)

  def function1ComposeSemigroup[A]: Semigroup[A => A] = instance(_ compose _)

  def function1ApplySemigroup[A, B: Semigroup]: Semigroup[A => B] =
    instance { (f: A => B, g: A => B) => a =>
      f(a) combine g(a)
    }
}
