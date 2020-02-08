package tutorial.libMyCats

trait Monoid[A] extends Semigroup[A] {

  // laws:
  // - associativity (inherited from Semigroup)
  // - left identity
  // - right identity

  def empty: A

  def (as: Seq[A]) combineAll =
    as.fold(empty)(_ combine _)
}

object Monoid {

  def apply[A: Monoid]: Monoid[A] = summon // summoner

  given as Monoid[Int] with Semigroup.IntSemigroup
    override def empty: Int = 0

  given as Monoid[String] with Semigroup.StringSemigroup
    override def empty: String = ""

  given as Monoid[Boolean] with Semigroup.BooleanSemigroup
    override def empty: Boolean = true

  given [A] as Monoid[List[A]] with Semigroup.ListSemigroup[A]
    override def empty: List[A] = List.empty[A]

  given [A: Semigroup] as Monoid[Option[A]] with Semigroup.OptionSemigroup[A]
    override def empty: Option[A] = Option.empty[A]

  given [K, V: Semigroup] as Monoid[Map[K, V]] with Semigroup.MapSemigroup[K, V]
    override def empty: Map[K, V] = Map.empty[K, V]

  given [A] as Monoid[A => A] with Semigroup.Function1AndThenSemigroup[A]
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

// should work without this helper
def [A: Monoid](as: Seq[A]) combineAll: A =
  Monoid[A].combineAll(as)
