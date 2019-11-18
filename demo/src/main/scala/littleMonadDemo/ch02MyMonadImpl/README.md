# Chapter 02:
# Monadic *compute*

To abstract over the effect types (*List*, *Option*,
*Future* etc.) we will use a Monad. A Monad provides us
(amongst many other operators) *flatMap*, and *map*. It
allows us to implement generic for-comprehensions, usable
with any effect type that has a Monad instance.

We define a *trait* Monad with the intrinsic abstract
methods *pure* and *flatMap* in the sub package
*libMyCats*. The Monad trait also provides
implementations for other concrete methods like *map*
and *flatten*.

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

The Monad trait has a type parameter *F[_]* - a place
holder for a type constructor that accepts exactly one
type parameter. In the implementations of the Monad trait - the Monad instances - we have to provide a
concrete type constructor like *List*, *Option*,
*Future* etc. and we implement the abstract methods
*pure* and *flatMap*.

These implementations are *implicit val*'s and best
placed in the Monad companion object (in sub package
*libMyCats*):

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

Method *pure* method lifts a single value of type *A*
into the context of the effect type. In the *List* Monad
implementaiton it creates a singleton list containing this
*A* value. For *Option* it creates a *Some* containing the
*A* value. Analogously for other Monad implementations.

The *flatMap* implementaions of the Monad instances just
delegate to the *flatMap* implementations of the respective
effect type (*List*, *Option*, *Opture* etc.) if these
types implement *flatMap*. (If they didn't we had to
provide a new impl.)

The Monad instances are defined as *implicit val*'s in the
Monad companion where they are found by the compiler
automaically during implicit resolution. Defined in the
Moand companion they need not be imported into local scope
at the call site.

Now the preconditions are met to define our generic method
*compute*, what we do in
*littleMonadDemo.libCompute.LibComputeWithMyCats.scala*:

```scala
import littleMonadDemo.libMyCats._

def compute[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
  for {
    i1 <- fInt1
    i2 <- fInt2
  } yield (i1, i2)
```

Our *compute* method takes as type parameter a type
constructor *F[_]* which is constrained to Monad, i.e.
*compute* can be used with any effect type that has a
Monad instance. In the previous impl of *compute* the
params had type *List[Int]*, *Option[Int]*, etc. Now
they are generic: *F[Int]*. The return type switched
from *List[(Int, Int)]* to *F[(Int, Int)]*.

At the call site (in this package) the generic *compute*
method (once imported) can be used like the non-generic
comnpute methods from chapter 01. But we squeezed three
similar implementations into one. We can use our
generic *compute* also with other effect types in case
we provide more Monad instances what we will do in
subsequent chapters.