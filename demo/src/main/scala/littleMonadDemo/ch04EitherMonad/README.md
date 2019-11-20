# Chapter 04

# Either Monad

We implemented a Monad instance for the effect types
_List_, _Option_, _Future_ and _Id_. These type constructors had on thing in common: They take only one
type parameter. And they are a good for the Monad trait.
The Monad trait requires a type constructor with one type parameter.

```scala
trait Monad[F[_]] {
  // ...
}
```

_Either_ has two type params ... what makes the implementation of the _Either_ Monad a bit more
difficult.

First we need to know that _map_ and _flatMap_ (and also
_flatten_) only apply to the right most type parameter
of a type. That means for _Either_: _map_ applied to a
_Left_ leaves the _Either_ unchanged. The same is true
for _flatMap_ and _flatten_.

To implement the _Either_ Monad we have to fix the left
type parameter.

A first approach could look like this:

```scala
// fix left type param to a String
type ErrorOr[A] = Either[String, A]

val errorOrMonad: Monad[ErrorOr] = new Monad[ErrorOr] {
  // ...
}
```

This works, but it is not a generic solution. We would
have to redefine the *Either* Monad for any other left
type.

Generic solution: We turn the _val_ into a _def_ and
parameterize it with the left type:

```scala
// fix left type param to a L
implicit def eitherMonad[L]: Monad[Either[L, ?]] = new Monad[Either[L, ?]] {
  // ...
}
```

The unknown right type param is designated with a _?_.
With the left type param fixed to _L_ we have created a
a type constructor that requires only one additional type
parameter which allows us to define an _Either_ Monad
instance.

The question mark syntax in _Monad[Either[L, ?]]_ is not
regular Scala syntax. For this to work we need to add
an extra compiler plugin to our _build.sbt_:
_kind-projector_

```scala
addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),
```

