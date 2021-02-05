package tutorial.libMyCats

trait Monoid[A] extends Semigroup[A]:

  // laws:
  // - associativity (inherited from Semigroup)
  // - left identity
  // - right identity

  def empty: A

  extension (as: Seq[A])
    def combineAll =
      as.fold(empty)(_ combine _)
end Monoid

object Monoid:

  def apply[A: Monoid]: Monoid[A] = summon // summoner

  def instance[A](z: A)(f: (A, A) => A): Monoid[A] = new Monoid[A] {
    override def empty: A = z
    extension (lhs: A)
      infix override def combine(rhs: A): A = f(lhs, rhs)
  }

  given Monoid[Int] = instance(0)(_ + _)

  given Monoid[String] = instance("")(_ ++ _)

  given Monoid[Boolean] = instance(true)(_ && _)

  given[A]: Monoid[List[A]] = instance(List.empty[A])(_ ++ _)

  given[A: Semigroup]: Monoid[Option[A]] =
    instance(Option.empty[A])(Semigroup.combineOptions)

  given[K, V: Semigroup]: Monoid[Map[K, V]] =
    instance(Map.empty[K, V])(Semigroup.combineMaps)

  given[A]: Monoid[A => A] = instance(identity[A](_))(_ andThen _)

  import scala.language.adhocExtensions
  
  def function1ComposeMonoid[A]: Monoid[A => A] =
    instance(identity[A](_))(_ compose _)
  
  def function1ApplyMonoid[A, B: Monoid]: Monoid[A => B] =
    instance { (_: A) =>
      Monoid[B].empty
    } { (f: A => B, g: A => B) => a =>
      f(a) combine g(a)
    }
end Monoid

// should work without this helper
extension [A: Monoid](as: Seq[A])
  def combineAll: A =
    Monoid[A].combineAll(as)
