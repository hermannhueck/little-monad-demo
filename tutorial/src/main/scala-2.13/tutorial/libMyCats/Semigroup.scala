package tutorial.libMyCats

trait Semigroup[A] {

  // laws:
  // - associativity

  def combine(lhs: A, rhs: A): A
}

object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = implicitly // summoner

  def instance[A](f: (A, A) => A): Semigroup[A] = new Semigroup[A] {
    override def combine(lhs: A, rhs: A): A = f(lhs, rhs)
  }

  implicit val intSemigroup: Semigroup[Int] =
    instance(_ + _)

  implicit val stringSemigroup: Semigroup[String] =
    instance(_ ++ _)

  implicit val booleanSemigroup: Semigroup[Boolean] =
    instance(_ && _)

  implicit def listSemigroup[A]: Semigroup[List[A]] =
    instance(_ ++ _)

  def combineOptions[A: Semigroup]: (Option[A], Option[A]) => Option[A] = {
    case (Some(la), Some(ra)) => Some(la combine ra)
    case (Some(la), None)     => Some(la)
    case (None, Some(ra))     => Some(ra)
    case (None, None)         => None
  }

  implicit def optionSemigroup[A: Semigroup]: Semigroup[Option[A]] =
    instance(combineOptions)

  // this impl deletes one value, if the same key is found in both maps
  // def mapCombine1[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
  //   lhs ++ rhs

  def mapCombine2[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] = {
    val rMapWithCommonKeysCombined =
      rhs
        .map {
          case (key, value) =>
            if (!lhs.keys.toSet.contains(key))
              key -> value
            else
              key -> (value combine lhs(key))
        }
    lhs ++ rMapWithCommonKeysCombined
  }

  implicit def mapSemigroup[K, V: Semigroup]: Semigroup[Map[K, V]] =
    instance((lMap, rMap) => mapCombine2(lMap, rMap))

  implicit def function1AndThenSemigroup[A]: Semigroup[A => A] =
    instance(_ andThen _)

  def function1ComposeSemigroup[A]: Semigroup[A => A] =
    instance(_ compose _)

  def function1ApplySemigroup[A, B: Semigroup]: Semigroup[A => B] =
    instance { (f: A => B, g: A => B) => a =>
      f(a) combine g(a)
    }
}
