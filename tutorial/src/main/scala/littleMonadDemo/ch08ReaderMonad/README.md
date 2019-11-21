# Chapter 08

# Reader Monad

The _Reader_ Monad is semantically identical to the
_Function1_ Monad.

_Function1_ itself just provides _apply_, _andThen_ and
_compose_. Extended functionality has to be provided
as extension methods for _Function1_.

We did that in the last chapter. We provided _flatMap_ and
_map_ in the _implicit class MonadSyntax_. _MonadSyntax_
was implemented in a generic way. It provided _map_ and
_flatMap_ for any effect type that does not have it's own
_map_ or _flatMap_. The compiler looks up extension methods
in implicit classes only if it doesn't find such a method
on the type in question. _List_ for example provides it's
own method _flatMap_. The compiler uses this one and does
not look up an extension method defined by an implicit
class (or an implicit conversion).

To provide a large bunch of functionality via extension
methods would be an overuse of this technique. With
_Reader_ we take a different approach. We wrap our
_Function1_ in a case class named _Reader_ and define
_map_, _flatMap_ and a bunch of other useful combinators
as methods of the _Reader_ case class. Implementing
_Reader_ ourselves we also have full control over the
_Reader_ companion and can provide smart constructors /
factory methods in the companion.

## _Reader#map_ and _Reader#flatMap_

But semantically there is no difference between _Function1_
and _Reader_. _Reader_ is just a case class wrapper around
a _Function1_ (called _run_) which allows us to implement
functionality directly on the wrapper. The implementation
of _Reader_ is located in the sub package _libMyCats_.

```scala
final case class Reader[P, A](run: P => A) {

  def map[B](f: A => B): Reader[P, B] =
    // Reader(p => f(run(p))) // is the same as:
    Reader(run andThen f)

  def flatMap[B](f: A => Reader[P, B]): Reader[P, B] =
    Reader(p => f(run(p)).run(p))
}
```

_Reader_ (like _Function1_) has two type params, we use
_P_ for the parameter or input type and _A_ for the
output or result type.

The wrapped _run_ is a function from P => A and _map_
gives us another function _f_ from _A_ => _B_. _map_ applies
_f_ to the result of _run_. The _map_ implementation
applies _run_ to the param _p_ and then it applies _f_
to the result of _run_. It is just a composition of _run_
and _f_ with _Function1#andThen_, that is again wrapped
in a _Reader_.

_flatMap_ is not much different. We also wrap the impl
on _Function1_ inside a _Reader_. The wrapped impl is a
function from _P_ => _B_. So the expression rigth to the
function _=>_ must give us a _B_. But the expression _f(run(p))_
gives us a _Reader[P, B]_. If we run this _Reader_ again
on the parameter _p_, we get a result of type _B_.

That's TDD: Type driven development!

## _Reader_ Monad instance

Having implemented _map_ and _flatMap_ on _Reader_ it is
easy to define a Monad instance in the companion object,
which implements _pure_ and delegates _flatMap_ to
_Reader#flatMap_.

```scala
object Reader {

  implicit def readerMonad[P]: Monad[Reader[P, ?]] = new Monad[Reader[P, ?]] {

    def pure[A](a: A): Reader[P, A] =
      Reader.pure(a)

    def flatMap[A, B](fa: Reader[P, A])(f: A => Reader[P, B]): Reader[P, B] =
      fa flatMap f
  }

  def pure[P, A](a: A): Reader[P, A] =
    Reader(_ => a)
}
```

We provide two implementations of _pure_:

1. The first one (_Reader.pure_) is defined directly on
   the companion. To lift a pure value of type _A_ into
   the _Reader_ context it ignores any possible input and
   returns the _A_ value as result: _Reader(\_ => a)_.
   _Reader.pure_ can be used in application code.

2. The other _pure_ is part of the _Reader_ Monad impl
   and just delegates to the _pure_ impl defined on the
   companion. This one is required for the implementation
   of our Monad instance.

As _Reader_ has a Monad instance it now can be used in
for-comprehensions and we can pass instances of _Reader_
to our generic _comput_ method.

```scala
val rPlus1: Reader[Int, Int]   = Reader(_ + 1)
val rDoubled: Reader[Int, Int] = Reader(_ * 2)

val rCompute: Reader[Int, (Int, Int)] =
  compute(rPlus1, rDoubled)
val result = rCompute.run(10) // (11, 20)
```

## Interpreter pattern

Used with _Reader_ our _compute_ method doesn't compute
the final result. In this case _compute_ just gives us
a _Reader_ back which allows us to compute a result for
a provided input. To get a result in the example above
we apply _rCompute.run_ to the input _10_.

This is the <u>interpreter pattern</u> which is typical
for functional programming.

1. First we construct a program by composing different
   functions together. (In Scala we typically use a
   for-comprehension for thisn purpose.) This can be seen
   as a program <u>description</u>.

2. Finally we invoke the program by providing some input.
   This is the program execution or <u>interpretation</u>.

We will see this pattern again when implementing the IO
Monad.

## _andThen_ and _compose_

As we want ot give our _Reader_ the same semantics as
_Function1_ provides we also implement _andThen_ and
_compose_ for _Reader_. These methods defined on _Reader_
basically delegate to the respective methods on
_Function1_.

```scala
final case class Reader[P, A](run: P => A) {

  // ... map, flatMap etc.

  def andThen[B](that: Reader[A, B]) =
    Reader(this.run andThen that.run)

  def andThen[B](that: A => B) =
    Reader(this.run andThen that.apply)

  def compose[B](that: Reader[B, P]) =
    Reader(this.run compose that.run)

  def compose[B](that: B => P) =
    Reader(this.run compose that.apply)
}
```

But we also supply two overloaded methods _andThen_ and
_compose_ such that we cannot only compose a _Reader_ with
another _Reader_, but also compose a _Reader_ with a
_Function1_. Thus you get a bit more convenience at the
call site. You are not forced to wrap a _Function1_ in a
_Reader_, in order to compose it with another _Reader_.

```scala
val plus1: Int => Int          = _ + 1
val doubled: Int => Int        = _ * 2
val rPlus1: Reader[Int, Int]   = Reader(plus1)
val rDoubled: Reader[Int, Int] = Reader(doubled)

// compose a Reader with another Reader
val res1 = (rPlus1 andThen rDoubled).run(10) // 22
// compose a Reader with a Function1
val res2 = (rPlus1 andThen doubled).run(10)  // 22
```

## Why is it called _Reader_?

The _Reader_'s input is only read and never written. That
is defferent in the _Writer_ Monad and in the _State_
Monad. That's why it's called _Reader_.

The input is supplied at the very end, when you finished
setting up your _Reader_ by composing it from smaller
components which are functions or other _Reader_'s.

This can also be seen as a kind of dependency injection.

## Small Database Reader example

In the DBReader example we do not operate on just _Int_
values. We work with an (admittedly very simple) database.
But it gives us at least an imagination what _Reader_
can be used for.

We use _Reader_ to wrap functions from _(Database => A)_, where _A_ represents the result type of a database query.

The _Database_ is the type of the input which we will
supply at the very end.

We define two methods _getUserId_ and _checkUserPassword_
which both return a _Reader[Database, A]_.

```scala
def getUserId(
    name: String
  ): Reader[Database, Option[Int]] = ???

def checkUserPassword(
    userId: Int,
    password: String
  ): Reader[Database, Boolean] = ???
```

In _checkLogin_ we compose these two _Readers_'s using a 
for-comprehension and get a _Reader_ back, which takes a
_Database_ and returns a _Boolean_ that indicates whether
the user's credentials were correct or not.

```scala
def checkLogin(
    username: String,
    password: String
  ): Reader[Database, Boolean] =
    for {
      optId     <- getUserId(username)
      id        <- Reader.pure(optId.getOrElse(-1))
      pwCorrect <- checkUserPassword(id, password)
    } yield pwCorrect
```

We just composed a wrapped function which is not yet
executed / interpreted. We had to inject a _Database_
instance to get the database queries executed in order
to receive the final query result.

```scala
val correct = checkLogin("kate", "acidburn").run(db)
```