# Chapter 07

# Function1 Monad

When implementing a Monad instance for _Function1_ let's
have a look at the _Either_ Monad again. _Function1_ has
two type parameters like _Either_. Again _flatMap_ and
_map_ apply to the rightmost type parameter and leave the
other ones untouched. For _Function1_ that means _flatMap_
and _map_ operate on the result of the _Function1_, not
on it's parameter.

With this in mind we can easily implement the _Function1_
Monad instance analogously to the _Either_ Monad instance.

We place the implicit implementation again into the _Monad_
companion object, as we cannot access the _Function1_ code
in the Scala standard library.

```scala
implicit def function1Monad[P]: Monad[Function1[P, ?]] = new Monad[Function1[P, ?]] {

def pure[A](a: A): Function1[P, A] =
  _ => a

def flatMap[A, B](fa: Function1[P, A])(f: A => Function1[P, B]): Function1[P, B] =
  p => f(fa(p))(p)
}
```

_X => Y_ is syntactic sugar for _Function1[X, Y]_.
Using this we finally get:

```scala
implicit def function1Monad[P]: Monad[P => ?] = new Monad[P => ?] {

def pure[A](a: A): P => A =
  _ => a

def flatMap[A, B](fa: P => A)(f: A => P => B): P => B =
  p => f(fa(p))(p)
}
```

Unlike _List_, _Option_, _Future_ and _Either_ _Function1_
does not provide it's own impl of _map_ and _flatMap_. To
be usable in for-comprehensions _Function1_ also requires
these as extension methods which we already defined in
_libMyCats/package.scala_ when implementing the Identity
Monad.

Here is the code again:

```scala
implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

  def flatMap[B](f: A => F[B]): F[B] =
    Monad[F].flatMap(fa)(f)

  def map[B](f: A => B): F[B] =
    Monad[F].map(fa)(f)
}
```

We now can use our monadic compute method with the
newly defined _Function1_ Monad.

```scala
val plus1: Int => Int = (x => x + 1)
val times2: Int => Int = (x => x * 2)

val fn2: Int => (Int, Int) =
  compute(plus1, times2)
val result: Int = fn2(10) // (11, 20)
```