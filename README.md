# Little Monad Tutorial in Scala

This is a little tutorial for Monads and functional
programming techniques which just assumes basic Scala
knowledge.

The tutorial is designed as a sequence of examples.
The base package is the package _tutorial_.
The examples are located in sub packages.

The description for all examples can be found in
_tutorial.docs_.

## Examples: Short Description:

1. Example 01 contains the motivating example, where I implement a
   _computeInts_ method three times using the same algorithm.
   The three _compute_ methods differ only in their effect
   types, which are _List_, _Option_ and _Future_.

2. In Example 02 I abstract over the effect type.
   I provide my own Monad trait in _libMyCats_ and
   Monad instances for _List_, _Option_ and _Future_.
   This allows me to define a polymorphic _compute_ method,
   which works with any effect type that has a Monad instance.

3. In Example 03 this chapter I implement the Identity Monad.

4. In this example implement the _Either_ Monad. _Either_
   differs from the other effect types in the number of
   it's type parameters. _Either_ has two type parameters,
   the other effect types only have one. This makes the
   implementation of the Monad instance a bit more
   complicated and requires the _kind-projector_ compiler
   plugin to be included in the build.

5. In this example we take a closer look at Scala functions.

6. In this example I re-emphasize the fact that functions
   are values and can be treated like other values. Here
   we stuff a bunch of functions into a _List_ and
   manipulate that list of functions with _map_ and _fold_.

7. In this example I implement a Monad instance for
   _Function1_.

8. Here I implement the _Reader_ Monad, which
   wraps a _Function1_.

9. In this example I implement the _IO_ Monad, which
   wraps a _Function0_.

10. Int this example I implement the type classes
    _Semigroup_ and _Monoid_ as well as instances
    for various types.
