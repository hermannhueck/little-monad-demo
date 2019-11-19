# Chapter 03

# Identity Monad

Our *compute* method accepts and returns *List[Int]* or
any other effect type that has a Monad instance:
*F[Int]: Monad*

It does not accept and return simple *Int* values. To be usable the *Int*s must always be wrapped inside a monadic
effect.

To support simple *Int*s we create a generic wrapper type for any *A* - the Identity Monad.

We could design this wrapper as a case class:

```scala
case class Id[A](value: A)
```

But it is easier to define *Id* as a type alias:

```scala
type Id[A] = A
```

Now it is quite simple to define a Monad instance for *Id*
inside the Monad companion. It's analogous to the Monad
instances for *List*, *Option* and *Future* we already
defined.

```scala
implicit val idMonad: Monad[Id] = new Monad[Id] {

override def pure[A](a: A): Id[A] =
    a

override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
    f(fa)
}
```

With this we can use our generic *compute* method also
with pure *Int* values aliased to *Id[Int]*.

```scala
val x: Id[Int] = 42
val y: Id[Int] = 42

// uses monadic compute method for F[Int]
val result = compute(x, y)
```

*x* and *y* must be type annotated to *Id[Int]* to be accepted as arguments to *compute*. *compute* only
takes *Int*s when they are wrapped in an effect.

Therefore we provide overloaded *compute* method in
littleMonadDemo.libCompute.LibComputeWithMyCats
which take two *Int*s and delegates to the monadic
*compute* annotating the *Int* values as *Id[Int]*.

```scala
def compute(i1: Int, i2: Int): (Int, Int) =
  compute(i1: Id[Int], i2: Id[Int])
```

Now we can use *compute* either with two *Int*s or with
effects on *Int*.

```scala
val x: Int = 42
val y: Int = 42

// uses compute method for pure Int values
val result = compute(x, y)
```
