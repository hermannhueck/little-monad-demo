# Chapter 02

# Monadic _compute_

To abstract over the effect types (_List_, _Option_,
_Future_ etc.) we will use a Monad. A Monad provides us
(amongst many other operators) _flatMap_, and _map_. It
allows us to implement generic for-comprehensions, usable
with any effect type that has a Monad instance.

We define a _trait_ Monad with the intrinsic abstract
methods _pure_ and _flatMap_ in the sub package
_libMyCats_. The Monad trait also provides
implementations for other concrete methods like _map_
and _flatten_.

```scala
trait Monad[F[_]] {

  // intrinsic abstract methods for Monad

  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  // other concrete methods implemented
  // in terms of flatMap and pure

  final def map[A, B](fa: F[A])(f: A => B): F[B] =
    flatMap(fa)(a => pure(f(a)))

  final def flatten[A](fa: F[F[A]]): F[A] =
    flatMap(fa)(a => a)
}
```

The Monad trait has a type parameter _F[_]_ - a place
holder for a type constructor that accepts exactly one
type parameter. In the implementations of the Monad trait - the Monad instances - we have to provide a
concrete type constructor like _List_, _Option_,
_Future_ etc. and we implement the abstract methods
_pure_ and _flatMap_.

These implementations are _implicit val_'s and best
placed in the Monad companion object (in sub package
_libMyCats_):

```scala
object Monad {

  implicit val listMonad: Monad[List] = new Monad[List] {

    override def pure[A](a: A): List[A] =
      List(a)

    override def flatMap[A, B](
      list: List[A]
    )(
      f: A => List[B]
    ): List[B] =
      list flatMap f
  }

  // ... other Monad instances
}
```

Method _pure_ method lifts a single value of type _A_
into the context of the effect type. In the _List_ Monad
implementaiton it creates a singleton list containing this
_A_ value. For _Option_ it creates a _Some_ containing the
_A_ value. Analogously for other Monad implementations.

The _flatMap_ implementaions of the Monad instances just
delegate to the _flatMap_ implementations of the respective
effect type (_List_, _Option_, _Opture_ etc.) if these
types implement _flatMap_. (If they didn't we had to
provide a new impl.)

The Monad instances are defined as _implicit val_'s in the
Monad companion where they are found by the compiler
automaically during implicit resolution. Defined in the
Moand companion they need not be imported into local scope
at the call site.

Now the preconditions are met to define our generic method
_compute_, what we do in
_littleMonadDemo.libCompute.LibComputeWithMyCats.scala_:

```scala
import tutorial.libMyCats._

def compute[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
  for {
    i1 <- fInt1
    i2 <- fInt2
  } yield (i1, i2)
```

Our _compute_ method takes as type parameter a type
constructor _F[_]_ which is constrained to Monad, i.e.
_compute_ can be used with any effect type that has a
Monad instance. In the previous impl of _compute_ the
params had type _List[Int]_, _Option[Int]_, etc. Now
they are generic: _F[Int]_. The return type switched
from _List[(Int, Int)]_ to _F[(Int, Int)]_.

At the call site (in this package) the generic _compute_
method (once imported) can be used like the non-generic
comnpute methods from chapter 01. But we squeezed three
similar implementations into one. We can use our
generic _compute_ also with other effect types in case
we provide more Monad instances what we will do in
subsequent chapters.