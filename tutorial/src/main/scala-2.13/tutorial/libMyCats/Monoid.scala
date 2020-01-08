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

  def apply[A: Monoid]: Monoid[A] = implicitly // summoner

  implicit val intMonoid: Monoid[Int] =
    new Semigroup.IntSemigroup with Monoid[Int] {
      override def empty: Int = 0
    }

  implicit val stringMonoid: Monoid[String] =
    new Semigroup.StringSemigroup with Monoid[String] {
      override def empty: String = ""
    }

  implicit val booleanMonoid: Monoid[Boolean] =
    new Semigroup.BooleanSemigroup with Monoid[Boolean] {
      override def empty: Boolean = true
    }

  implicit def listMonoid[A]: Monoid[List[A]] =
    new Semigroup.ListSemigroup[A] with Monoid[List[A]] {
      override def empty: List[A] = List.empty[A]
    }

  implicit def optionsMonoid[A: Semigroup]: Monoid[Option[A]] =
    new Semigroup.OptionSemigroup[A] with Monoid[Option[A]] {
      override def empty: Option[A] = Option.empty[A]
    }

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] =
    new Semigroup.MapSemigroup[K, V] with Monoid[Map[K, V]] {
      override def empty: Map[K, V] = Map.empty[K, V]
    }
}
