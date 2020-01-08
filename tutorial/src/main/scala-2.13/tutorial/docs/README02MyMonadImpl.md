# Chapter 02

# Monadic _compute_

To abstract over the effect types (_List_, _Option_,
_Future_, etc.), we will use a Monad. A Monad provides us with
(among many other operators) _flatMap_ and _map_. It
allows us to implement generic for-comprehensions, usable
with any effect type that has a Monad instance.

We define a _trait_ Monad with the intrinsic abstract
methods _pure_ and _flatMap_ in the sub package
_libMyCats_. The Monad trait also provides
implementations for other concrete methods like _map_
and _flatten_.

```scala
trait Monad[F[_]] {

  // intrinsic abstract Monad methods

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

The Monad trait has a type parameter _F[\_]_ - a place
holder for a type constructor that accepts exactly one
type parameter. In the implementations of the Monad
trait - the Monad instances - we have to provide a
concrete type constructor like _List_, _Option_,
_Future_, etc. and have to implement the abstract methods
_pure_ and _flatMap_.

These implementations are _implicit val_'s and _def_'s, best
placed in the Monad companion object (in sub package
_libMyCats_):

```scala
object Monad {

  def apply[F[_]: Monad]: Monad[F] = implicitly[Monad[F]] // summoner

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

The _pure_ method lifts a single value of type _A_
into the context of the effect type. In the _List_ Monad
implementation it creates a singleton list containing this
_A_ value. For _Option_ it creates a _Some_ containing the
_A_ value. It works analogously for other Monad implementations.

The _flatMap_ implementations of the Monad instances just
delegate to the _flatMap_ implementations of the respective
effect type (_List_, _Option_, _Future_, etc.) if these
types implement _flatMap_. If an effect type doesn't provide _flatMap_
(see subsequent examples _Id_ Monad and _Function1_ Monad),
we have to write it ourselves.

The Monad instances are defined as _implicit val_'s (or _def_'s) in the
Monad companion object where they are found by the compiler
automatically during implicit resolution. As they are defined in the
Monad companion object, there's no need to import them
into local scope at the call site.

In the Monad companion object above we also provide a summoner,
an _apply_ method which summons a Monad instance for the effect type *F[_]*
from the implicit scope.

To provide _flatMap_ and _map_ for any Monad we have to define them
as extension methods in an implicit class for any *Monad[F[_]]*.
The implicit class implementation is located in the _libMyCats_ package object,
where it can be resolved if *libMyCats._* is imported at the call site.
The implementation of these two method uses the provided summoner to summon
a Monad instance for *F[_]* and invoke _flatMap_ or _map_ on that instance.

```scala
implicit class MonadSyntax[F[_]: Monad, A](fa: F[A]) {

  def flatMap[B](f: A => F[B]): F[B] =
    Monad[F].flatMap(fa)(f)

  def map[B](f: A => B): F[B] =
    Monad[F].map(fa)(f)
}
```

Now the preconditions are met to define our generic _computeInts_ method,
which we do in the package object _tutorial.libMyCats_:

```scala
def computeInts[F[_]: Monad](fInt1: F[Int], fInt2: F[Int]): F[(Int, Int)] =
  for {
    i1 <- fInt1
    i2 <- fInt2
  } yield (i1, i2)
```

Our _computeInts_ method takes as type parameter a type
constructor *F[_]* which is constrained to Monad using
the context bound *[F[_]: Monad]* (which is equivalent
to an implicit parameter). I.e. _computeInts_ can be
used with any effect type that has a Monad instance.
In the previous implementation of _computeInts_ the
params had type _List[Int]_, _Option[Int]_, etc. Now
they are generic: _F[Int]_. The return type changed
from _List[(Int, Int)]_ to _F[(Int, Int)]_.

We do not really operate on _Int_ values, we just tuple
the _Int_'s up to pairs. As this operation is not _Int_-specific,
we can also abstract the _Int_'s away and replace them with
the type parameters _A_ and _B_.

```scala
def compute[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
  for {
    a <- fa
    b <- fb
  } yield (a, b)
```

At the call site the generic _compute_
method (once imported) can be used like the non-generic
_computeInts_ methods from chapter 01. By using the monadic
abstraction we reduced boilerplate and squeezed three
similar implementations into one.

We can use our generic _compute_ also with any other effect
type in case we provide a Monad instance for that type,
which we will do in subsequent chapters.

```scala
def compute2[F[_], A, B](fa: F[A], fb: F[B])(implicit m: Monad[F]): F[(A, B)] =
  fa.flatMap { a =>
    fb.map { b =>
      (a, b)
    }
  }
```

_compute2_ shows the desugared version of _compute_. The
for-comprehension is desugared by the compiler to a sequence
of _flatMap_'s followed by _map_. The context bound in the
signature is desugared to an implicit parameter.
