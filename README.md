# Little Monad Tutorial in Scala

This is a little tutorial for Monads and functional programming techniques which just assumes basic Scala
knowledge.

The tutorial is designed as a sequence of steps/chapters
which are built one on top of the other. The base package
is the package _tutorial_. This contains sub packages named
as chapters. Each sub package contains the code for a step
and a README.md decribing the step of that chapter.

1. _ch01ForComprehensions_:<br/>
   contains the motivating example, where I implement a
   _compute_ method three times using the same algorithm.
   The three _compute_ methods differ only in their effect
   types, which are _List_, _Option_ and _Future_.

2. _ch02MyMonadImpl_:<br/>
   In this chapter/step I abstract over
   the effect type. I provide my own Monad trait in
   _libMyCats_ and Monad instances for _List_, _Option_ and
   _Future_. This allows me to define a polymorphic
   _compute_ method in the sub package _libCompute_, which
   works with any effect type as long as it is a Monad.

3. _ch03IdentityMonad_:<br/>
   In this chapter I implement the Identity Monad.

4. _ch04EitherMonad_:<br/>
   In this chapter I implement the _Either_ Monad. _Either_
   differs from the other effect types in the number of
   it's type parameters. _Either_ has two type params, the other effect types only have one. This makes the implementation of the Monad instance a bit more
   complicated and requires the _kind-projector_ compiler
   plugin to be included in the build.

5. _ch05Functions_:<br/>
   In this chapter we take closer look at Scala functions.

6. _ch06FunctionsAreValues_:<br/>
   In this chapter I re-emphasize the fact that functions
   are values and can be treated like other values. Here
   we stuff a bunch of functions into a _List_ and
   manipulate that list of functions with _map_ and _fold_.

7. _ch07Function1Monad_:<br/>
   In this chapter I implement a Monad instance for
   _Function1_.

8. _ch08ReaderMonad_:<br/>
   In this chapter I implement the _Reader_ Monad, which
   wraps a _Function1_.

9. _ch09IOMonad_:<br/>
   In this chapter I implement the _IO_ Monad, which
   wraps a _Function0_.

- _libMyCats_:<br/>
  is my little library of categories providing the Monad
  trait and a bunch of different Monad instances which I
  implement in the above chapters.

- _libCompute_:<br/>
  contains a polymorphic compute method which works with
  any effect type that has a Monad instance (implemented
  in chapter 02).