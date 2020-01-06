# Chapter 03

# Identity Monad

Our _compute_ method accepts and returns _List[A]_ or
any other effect type that has a Monad instance:
_F[A]: Monad_

It does not accept and return simple _A_ values. To be
usable the _A_'s must always be wrapped inside a monadic
effect.

To support simple _A_'s we create a generic wrapper
type for any _A_ - the Identity Monad.

We could design this wrapper as a case class:

```scala
case class Id[A](value: A)
```

But it's sufficient to define _Id_ as a type alias:

```scala
type Id[A] = A
```

Now it's quite simple to define a Monad instance for _Id_
inside the Monad companion. It's analogous to the Monad
instances for _List_, _Option_ and _Future_ we already
defined.


```scala
object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

  implicit val idMonad: Monad[Id] = new Monad[Id] {
    override def pure[A](a: A): Id[A] =
      a
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      f(fa)
  }
}
```

_List_, _Option_ and other types from the Scala standard
library already have methods _map_ and _flatMap_. Hence
they can directly be used in for-comprehensions. _Id_
doesn't have _map_ and _flatMap_.

But we can add them as extension methods (defined with
an implicit class), which are specific for the _Id_ type:

```scala
implicit class IdSyntax[A](fa: Id[A]) {

  def flatMap[B](f: A => Id[B]): Id[B] =
    Monad[Id].flatMap(fa)(f)

  def map[B](f: A => B): Id[B] =
    Monad[Id].map(fa)(f)
}
```

Alternatively we can use a generic solution which works for
any type that has a Monad instance, but doesn't provide
it's own _flatMap_ and _map_.

```scala
implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

  def flatMap[B](f: A => F[B]): F[B] =
    Monad[F].flatMap(fa)(f)

  def map[B](f: A => B): F[B] =
    Monad[F].map(fa)(f)
}
```

As already shown in example 02 this implementation (using _Monad[F]_)
relies on the Monad summoner provided in the _Monad_ companion object.

With this we can use our parametric _compute_ method also
with pure _Int_ values type-aliased to _Id[Int]_.

```scala
val x: Id[Int] = 42
val y: Id[Int] = 42

// uses monadic compute method for F[Int]
val result = compute(x, y)
```

_x_ and _y_ must be type annotated to _Id[Int]_ to be accepted as arguments to _compute_. _compute_ only
takes _Int_'s when they are wrapped in an effect.

Therefore we provide an overloaded _compute_ method in
the package object _tutorial.libCompute_ which takes two
values _A_ and _B_ and delegates to the monadic _compute_,
annotating the _A_ and _B_ values as _Id[A]_ and _Id[B]_.


```scala
def compute[A, B](a: A, b: B): (A, B) =
  compute(a: Id[A], b: Id[B])
```

If _A_ and _B_ are of type _Int_ we can use _compute_ either
with two _Int_'s or with effects on _Int_.

```scala
val x: Int = 42
val y: Int = 42

// uses compute method for pure Int values
val result = compute(x, y)
```
