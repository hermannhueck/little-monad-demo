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

  def instance[A](z: A)(f: (A, A) => A): Monoid[A] = new Monoid[A] {
    override def empty: A                   = z
    override def combine(lhs: A, rhs: A): A = f(lhs, rhs)
  }

  implicit val intMonoid: Monoid[Int] =
    instance(0)(_ + _)

  implicit val stringMonoid: Monoid[String] =
    instance("")(_ ++ _)

  implicit val booleanMonoid: Monoid[Boolean] =
    instance(true)(_ && _)

  implicit def listMonoid[A]: Monoid[List[A]] =
    instance(List.empty[A])(_ ++ _)

  implicit def optionsMonoid[A: Semigroup]: Monoid[Option[A]] =
    instance(Option.empty[A])(Semigroup.combineOptions)

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] =
    instance(Map.empty[K, V])(Semigroup.combineMaps(_, _))

  implicit def function1AndThenMonoid[A]: Monoid[A => A] =
    instance(identity[A](_))(_ andThen _)

  def function1ComposeMonoid[A]: Monoid[A => A] =
    instance(identity[A](_))(_ compose _)

  def function1ApplyMonoid[A, B: Monoid]: Monoid[A => B] =
    instance { (_: A) => Monoid[B].empty } { (f: A => B, g: A => B) => a => f(a) combine g(a) }
}
