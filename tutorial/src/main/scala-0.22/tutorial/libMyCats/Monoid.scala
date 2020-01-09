package tutorial.libMyCats

trait Monoid[A] extends Semigroup[A] {

  // laws:
  // - associativity (inherited from Semigroup)
  // - left identity
  // - right identity

  def empty: A

  def combineAll(as: Seq[A]) =
    as.fold(empty)(combine)
}

object Monoid {

  def apply[A: Monoid]: Monoid[A] = summon // summoner

  given Monoid[Int] with Semigroup.IntSemigroup
    override def empty: Int = 0

  given Monoid[String] with Semigroup.StringSemigroup
    override def empty: String = ""

  given Monoid[Boolean] with Semigroup.BooleanSemigroup
    override def empty: Boolean = true

  given [A]: Monoid[List[A]] with Semigroup.ListSemigroup[A]
    override def empty: List[A] = List.empty[A]

  given [A: Semigroup]: Monoid[Option[A]] with Semigroup.OptionSemigroup[A]
    override def empty: Option[A] = Option.empty[A]

  given [K, V: Semigroup]: Monoid[Map[K, V]] with Semigroup.MapSemigroup[K, V]
    override def empty: Map[K, V] = Map.empty[K, V]

  given [A]: Monoid[A => A] with Semigroup.Function1AndThenSemigroup[A]
    override def empty: A => A = identity

  import scala.language.adhocExtensions
  
  def function1ComposeMonoid[A]: Monoid[A => A] =
    new Semigroup.Function1ComposeSemigroup[A] with Monoid[A => A] {
      override def empty: A => A = identity
    }
  
  def function1ApplyMonoid[A, B: Monoid]: Monoid[A => B] =
    new Semigroup.Function1ApplySemigroup[A, B] with Monoid[A => B] {
      override def empty: A => B = _ => Monoid[B].empty
    }
}

def [A: Monoid](as: Seq[A]) combineAll: A =
  Monoid[A].combineAll(as)
