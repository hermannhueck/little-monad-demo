# Chapter 05

# Functions

Before writing the _Function1_ Monad, we have to get a
good understanding of Scala functions.

Scala has 23 function traits, _Function0_, _Function1_, ...
up to _Function22_. The number encoded in the name
designates the number of arguments a function accepts.
The number of type parameters is one more than the number
of arguments, as they all require one additional type parameter
for the return type. E.g. _Function2_ takes two
arguments, but it has three type parameters.

```scala
package scala

trait Function2[-T1, -T2, +R] { // simplified
  def apply(t1: T1, t2: T2): R
}
```

A concrete function is just an instance of the respective
function trait.

```scala
val f2Verbose: Function2[Int, Int, Int] =
  new Function2[Int, Int, Int] {
    def apply(x: Int, y: Int): Int = x + y
  }
// f2Verbose: (Int, Int) => Int = <function2>
```

This is verbose and Scala has syntactic sugar for it:

```scala
// All versions are semantically identical.
val f2a: (Int, Int) => Int        = (x, y) => x + y
// f2a: (Int, Int) => Int = <function2>
val f2b: Function2[Int, Int, Int] = _ + _
// f2b: (Int, Int) => Int = <function2>
val f2c                           = (x: Int, y: Int) => x + y
// f2c: (Int, Int) => Int = <function2>
val f2d                           = (_: Int) + (_: Int)
// f2d: (Int, Int) => Int = <function2>
```

The mostly used function is _Function1_. Scala also
provides a shortcut for the _Function1_ type:
_A => B_ desugars to _Function1[A, B]_.

```scala
val f1a: Function1[Int, Int] = x => x + 1
// f1a: Int => Int = <function1>
val f1b: Int => Int          = x => x + 1
// f1b: Int => Int = <function1>
val f1c: Int => Int          = _ + 1
// f1c: Int => Int = <function1>
```

This is just a repetition of basic Scala knowledge.

_Function1_ plays a central role in the Scala function
system ... for two reasons:

1. _Function1_ is composable.
2. Other functions (except _Function0_) can easily be
   converted to a _Function1_.

## Composition of _Function1_

_Function1_ provides two methods for composition in the
Scala standard library: _andThen_ and _compose_.

```scala
package scala

trait Function1[-T, +R] { // simplified

  def apply(t: T): R
  
  // applies this function first and then that
  def andThen(that: R => R2): R2 =
    x => that.apply(this.apply(x))

  // applies that function first and then this
  def compose(that: T0 => T): R =
    x => this.apply(that.apply(x))
}
```

_compose_ is just _andThen_ with the arguments flipped.

```scala
val plus1: (Int => Int)  = _ + 1
// plus1: Int => Int = <function1>
val square: (Int => Int) = x => x * x
// square: Int => Int = <function1>

assert {
  (plus1 andThen square)(42) ==
    (square compose plus1)(42)
}
```

_Function2_ to _Function22_ do not provide _andThen_ and
_compose_, as the number of parameters is not equal to the
number of return values.

Instead these functions provide means to convert them to
_Function1_.

## Conversions of _FunctionN_ to _Function1_

_Function2_ to _Function22_ can be converted to
_Function1_ in two ways:

1. Tupling
2. Currying

### Tupling of _FunctionN_ (where N > 1)

_FunctionN_ can be tupled. They provide a method
_tupled_, which wraps all args in a tuple and returns
a _Function1_ which takes a tuple as parameter.

```scala
val add: Function2[Int, Int, Int]  = _ + _
// add: (Int, Int) => Int = <function2>

val addTupled: ((Int, Int)) => Int = add.tupled
// addTupled: (Int, Int) => Int = scala.Function2$$Lambda$6634/0x0000000801bb6040@43b41086

val pair: (Int, Int)  = (21, 21)
// pair: (Int, Int) = (21, 21)
val sumFromTuple: Int = addTupled(pair) // 42
// sumFromTuple: Int = 42
```

### Currying of _FunctionN_ (where N > 1)

More useful than tupling is currying. All _FunctionN_
provide a method _curried_, which turns this _FunctionN_
into a _Function1_ that accepts the first argument and
returns another function which then takes the remaining
arguments (also one by one).

This allows to partially apply the functions to the
arguments. For this reason "Currying" is also called
"partial application".

In the simplest case of _Function2_ (see below) we curry
the *add* function into a _Function1_, which takes an
_Int_ and returns another _Function1_, that takes an
_Int_ and returns an _Int_. This is *addCurried*. We
apply *addCurried* to the _Int_ value 40 and get back
another _Function1_ taking an _Int_ and returning an
_Int_. We name it _plus40_. Applying *plus40* to
another _Int_ in a second step gives us the final _Int_
result.

```scala
val add: Function2[Int, Int, Int]  = _ + _
// add: (Int, Int) => Int = <function2>

val addCurried: Int => Int => Int = add.curried
// addCurried: Int => Int => Int = scala.Function2$$Lambda$16291/0x00000008047a5040@77f2a04b

val plus40   = addCurried(40)
// plus40: Int => Int = scala.Function2$$Lambda$16292/0x0000000804795040@289083eb
val sum: Int = plus40(2) // 42
// sum: Int = 42
```

_Function1_ is much better composable than _FunctionN_.
It is also the conversion target of _FunctionN_
(where N > 1).

&nbsp;

We pointed out the importance of _Function1_ in order
to legitimate our choice of defining a Monad instance
for it or wrapping it in a case class (Reader Moand) in
the subsequent chapters.

We will later have a look at _Function0_ when we implement the IO Monad.