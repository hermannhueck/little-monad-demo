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
}

def [A: Monoid](as: Seq[A]) combineAll: A =
  Monoid[A].combineAll(as)
