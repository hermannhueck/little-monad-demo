# Chapter 07

# Function1 Monad

While implementing a Monad instance for _Function1_, let's
have a look at the _Either_ Monad again. _Function1_ has
two type parameters like _Either_. Again _flatMap_ and
_map_ apply to the rightmost type parameter and leave the
other ones untouched. For _Function1_ this means _flatMap_
and _map_ operate on the result of the _Function1_, not
on it's argument.

With this in mind we can easily implement the _Function1_
Monad instance analogously to the _Either_ Monad instance.

We place the implicit implementation again into the _Monad_
companion object, as we cannot access the _Function1_ code
in the Scala standard library.

```scala mdoc:invisible
// mdoc:invisible
trait Monad[F[_]] {

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  final def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))
  final def flatten[A](fa: F[F[A]]): F[A] =
    flatMap(fa)(identity)
}
// mdoc:invisible
```

```scala
object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

  implicit def function1Monad[P]: Monad[Function1[P, ?]] = new Monad[Function1[P, ?]] {
    def pure[A](a: A): Function1[P, A] =
      _ => a
    def flatMap[A, B](fa: Function1[P, A])(f: A => Function1[P, B]): Function1[P, B] =
      p => f(fa(p))(p)
  }
}
```

_Function1_ (like _Either_) also has two type parameters.
Again we used the _?_ syntax to define a Monad instance
for _Function1_.

_X => Y_ is syntactic sugar for _Function1[X, Y]_.
Using this as a replacment our _function1Monad_ looks like this:

```scala mdoc
object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

  implicit def function1Monad[P]: Monad[P => ?] = new Monad[P => ?] {
    def pure[A](a: A): P => A =
      _ => a
    def flatMap[A, B](fa: P => A)(f: A => P => B): P => B =
      p => f(fa(p))(p)
  }
}
```

Unlike _List_, _Option_, _Future_ and _Either_, _Function1_
does not provide it's own implementation of _map_ and _flatMap_.
In order to be usable in for-comprehensions, _Function1_ also requires
these as extension methods. We already defined them in
_libMyCats/package.scala_ when implementing the Identity
Monad.

Here is the code again:

```scala mdoc
implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

  def flatMap[B](f: A => F[B]): F[B] =
    Monad[F].flatMap(fa)(f)

  def map[B](f: A => B): F[B] =
    Monad[F].map(fa)(f)
}
```

We now can use our monadic compute method with the
newly defined _Function1_ Monad.

```scala mdoc:invisible
// mdoc:invisible
def compute[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
  for {
    a <- fa
    b <- fb
  } yield (a, b)
// mdoc:invisible
```

```scala mdoc
val plus1: Int => Int = (x => x + 1)
val times2: Int => Int = (x => x * 2)

val fn2: Int => (Int, Int) =
  compute(plus1, times2)
val result: (Int, Int) = fn2(10) // (11, 20)
```

With _Function1_ Monad the _compute_ method does not
give us a tupled result. Here _compute_ returns a composed
_Function1_, that must be applied to a value to give
us a tuple result.
