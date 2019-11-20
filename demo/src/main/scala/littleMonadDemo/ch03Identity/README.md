# Chapter 03

# Identity Monad

Our _compute_ method accepts and returns _List[Int]_ or
any other effect type that has a Monad instance:
_F[Int]: Monad_

It does not accept and return simple _Int_ values. To be usable the _Int_'s must always be wrapped inside a monadic
effect.

To support simple _Int_'s we create a generic wrapper type for any _A_ - the Identity Monad.

We could design this wrapper as a case class:

```scala
case class Id[A](value: A)
```

But it is easier to define _Id_ as a type alias:

```scala
type Id[A] = A
```

Now it is quite simple to define a Monad instance for _Id_
inside the Monad companion. It's analogous to the Monad
instances for _List_, _Option_ and _Future_ we already
defined.

```scala
implicit val idMonad: Monad[Id] = new Monad[Id] {

override def pure[A](a: A): Id[A] =
    a

override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
    f(fa)
}
```

With this we can use our generic _compute_ method also
with pure _Int_ values aliased to _Id[Int]_.

```scala
val x: Id[Int] = 42
val y: Id[Int] = 42

// uses monadic compute method for F[Int]
val result = compute(x, y)
```

_x_ and _y_ must be type annotated to _Id[Int]_ to be accepted as arguments to _compute_. _compute_ only
takes _Int_'s when they are wrapped in an effect.

Therefore we provide overloaded _compute_ method in
littleMonadDemo.libCompute.LibComputeWithMyCats
which take two _Int_'s and delegates to the monadic
_compute_ annotating the _Int_ values as _Id[Int]_.

```scala
def compute(i1: Int, i2: Int): (Int, Int) =
  compute(i1: Id[Int], i2: Id[Int])
```

Now we can use _compute_ either with two _Int_'s or with
effects on _Int_.

```scala
val x: Int = 42
val y: Int = 42

// uses compute method for pure Int values
val result = compute(x, y)
```
