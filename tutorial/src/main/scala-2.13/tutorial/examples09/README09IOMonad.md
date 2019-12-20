# Chapter 09

# IO Monad

The main purpose of the _IO_ Monad is the delayed
execution of side-effects.

## What are side-effects?

A side-effect is everything that might break referential
transparency, e.g.:

- mutating variables
- throwing exceptions
- reading from and writing to the console
- reading from and writing to the file system
- logging
- database access
- network access

... all the things that constitute a real world program.

## Interpreter pattern again

In pure functional programming we describe the side-effects
in functions (without executing them), we compose these
functions to a program and we then execute them "at the
end of the world".

This again is the <u>interpreter pattern</u> we already
used in our _Reader_ Monad examples.

While defining and composing our side-effecting functions
our program remains referentially transparent as long as we
do not execute it. This part should represent 90 % or more of
our entire program. The side-effects do not happen before
we execute the program. This can be a final one-liner.

## Using _Function0_

```scala
object HelloWithFunction0 extends App {

  lineStart() pipe println

  val promptForName: () => Unit =
    () => print("Whats your name?  ")

  val readInput: () => String =
    () => scala.io.StdIn.readLine()

  def printHello(name: String): () => Unit =
    () => println(s"Hello $name!")

  promptForName()
  val name = readInput()
  val printHelloWithName: () => Unit =
    printHello(name)
  printHelloWithName()
}
```

In this small example we define two _Function0_'s and a
method which returns a _Function0_. These _Function0_
instances are executed one by one by appending a pair
of parentheses to the end of these functions.

The three functions cannot easily be composed, because
_Function0_ does not provide _map_ and _flatMap_.

Aside: _() => A_ is syntactic sugar for _Function0[A],
where the empty parens denote an empty parameter list
and \_A_ is the polymorphic return type.

Just as with _Function1_ there are two approaches to
solve this problem:

1. We could define a _Function0_ Monad - possible but not
   very handy. We won't follow this path.
2. We wrap _Function0_ in a case class and provide _map_
   and _flatMap_ on the wrapper. This is _IO_.

## _IO_

Like _Reader_, _IO_ is a case class wrapper around a
function. _Reader_ wrapped a _Function1_, _IO_ wraps a
_Function0_.

The wrapped function - this time we call it
_unsafeRun_ - delays the side-effects until executed.

We first implement _map_ and _flatMap_ for _IO_.

```scala
final case class IO[A](unsafeRun: () => A) {

  def map[B](f: A => B): IO[B] =
    IO(() => f(unsafeRun()))

  def flatMap[B](f: A => IO[B]): IO[B] =
    IO(() => f(unsafeRun()).unsafeRun())
}
```

_map_ applies _f_ to the result of _unsafeRun()_ and wraps
the whole _Function0_ in an _IO_. _flatMap_ also applies _f_
to the result of _unsafeRun()_. This gives us an _IO[B]_
where we need a _B_. So we invoke _unsafeRun()_ again on
the result of _f(unsafeRun())_ and get a _B_. Then we wrap
the _Function0_ in an _IO_.

Having defined _map_ and _flatMap_ for _IO_ we can implement
a Monad instance in the _IO_ companion object analogously to
_Reader_.

```scala
object IO {

  implicit val ioMonad: Monad[IO] = new Monad[IO] {

    override def pure[A](a: A): IO[A] =
      IO.pure(a)

    override def flatMap[A, B](io: IO[A])(f: A => IO[B]): IO[B] =
      io flatMap f
  }

  def pure[A](a: A): IO[A] =
    IO(() => a)
}
```

Now we are able to compose the console input and output
functions from above into a little "Hello" program which we
execute at the very end. As usual we use a for-comprehension.
The impure invocation of the program becomes a one-liner.

```scala
object IOAppHello extends App {

  lineStart() pipe println

  val hello: IO[Unit] = for {
    _    <- IO(() => print("What's your name?  "))
    name <- IO(() => scala.io.StdIn.readLine())
    _    <- IO(() => println(s"Hello $name!\n"))
  } yield ()

  hello.unsafeRun()
}
```

Even if it doesn't make much sense, we can use our new
_IO_ Monad with our polymorphic _compute_ method which
is supposed to work with any Monad.

```scala
object IOAppCompute extends App {

  lineStart() pipe println

  val io1 = IO.pure(40)
  val io2 = IO.pure(2)

  val ioCompute: IO[(Int, Int)] = compute(io1, io2)
  val pair: (Int, Int) = ioCompute.unsafeRun() // (40, 2)
}
```

Again, the result of _compute_ is not the final result of
the computation. The result of _compute_ is another _IO_
which returns the final result when executed. _compute_
used with _IO_ (like _Reader_) returns a new program.
Unlike _Reader_, _IO_ doesn't require any input parameter,
as it encapsulates a _Function0_, not a _Function1_.

This is the gist of _IO_.

But of course we can add more functionality to it, thus
giving the user more convenience when using _IO_.

## _unsafeRunToTry_, _unsafeRunToEither_, _unsafeRunToFuture_

When we invoke _unsafeRun_ bad things can happen.
_unsafeRun_ might throw an exception.

_unsafeRunToTry_ catches this possible exception and
returns a _Try[A]_ instead of an _A_. But sometimes
we would prefer an _Either_ at the call site. For this
purpose we also provide _unsafeRunToEither_, so that
the call site user can choose the best fit for her.

We also provide _IO#unsafeRunToFuture_, convenient for the
caller who wants to run the _IO_ asynchronously.
_unsafeRunToFuture_ provides an implicit
_ExecutionContext_ needed to create the _Future_.

The implementation:

```scala
def unsafeRunToTry(): Try[A] =
  Try(unsafeRun())

def unsafeRunToEither(): Either[Throwable, A] =
  unsafeRunToTry().toEither

def unsafeRunToFuture(implicit ec: ExecutionContext): Future[A] =
  Future(unsafeRun())
```

The call site:

```scala
val io = IO { () =>
  somePossiblyFailingComputation(42)
}

io
  .unsafeRunToTry()
  .fold(t => println(t.getMessage), v => println(v))

io
  .unsafeRunToEither()
  .fold(t => println(t.getMessage), v => println(v))

implicit val ec: ExecutionContext = ExecutionContext.global

io
  .unsafeRunToFuture
  .onComplete {
    case Failure(t) => println(t.getMessage)
    case Success(v) => println(v)
  }
```

This turns our _IO_ into the classical Scala data
structures. But we can also go the other way round.

## _fromTry_, _fromEither_ and _fromFuture_

These methods turn a _Try_, _Either_ or _Future_ into an
IO. They are factory methods defined in the _IO_ companion
object which creates a new _IO_ from these data structures.

The implementation:

```scala
  def fromTry[A](tryy: Try[A]): IO[A] =
    IO(() => tryy.get)

  def fromEither[A](either: Either[Throwable, A]): IO[A] =
    IO(() => either.toTry.get)

  // by name param prevents the future from
  // being constructed and run immediately
  def fromFuture[A](future: => Future[A], timeout: Duration = Duration.Inf): IO[A] =
    IO(() => Await.result(future, timeout))
    // !!! Blocking when executed !!!
```

_fromTry_ takes the value from the _Try_ with _Try#get_.
This might again throw an exception when executed. It
won't hurt us as this is done in a _Function0_ which is
not yet executed. The same is true for _fromEither_,
where the _Either_ instance is turned into a _Try_, from
which we then get the wrapped value (or the exception).

_IO.fromFuture_ takes a _Future_ as a by name parameter.
Call by name prevents the future from being executed
eagarly. With call by value the future would have been
executed before being passed to the _fromFuture_ method.

The call site:

```scala
val tryy =
  Try(somePossiblyFailingComputation(42))
val io1 = IO.fromTry(tryy)
io1.unsafeRunToXXX


val either =
  Try(somePossiblyFailingComputation(42)).toEither
val io2 = IO.fromEither(either)
io2.unsafeRunToXXX


implicit val ec: ExecutionContext = ExecutionContext.global

def getFuture(): Future[Int] = Future {
  somePossiblyFailingComputation(42)
}

val io3 = IO.fromFuture(getFuture())
io3.unsafeRunToXXX
```

To execute _io1_, _io2_ and _io3_ any _unsafeRunXXX_
invocation can be applied, depending on what kind of result
the caller needs.

## _IO.eval_

```scala
def eval[A](thunk: => A): IO[A] =
  IO(() => thunk)
```

_IO.eval_ takes a thunk, a computation which returns a
result of type _A_, but may also throw an exception
or produce a side-effect.

The thunk is passed by name which prevents the thunk from
being executed before being passed to _eval_. The implementation of
_eval_ also doesn't evaluate it; it creates a
_Function0_ from the thunk: _() => thunk_ and wraps it in
an _IO_, which results in: _IO(() => thunk)_

This makes it a bit more convenient at the call site to
create an _IO_'s using _eval_.

```scala
// IOAppHelloWithEval.scala
val hello: IO[Unit] = for {
  _    <- IO.eval(print("What's your name?  "))
  name <- IO.eval(scala.io.StdIn.readLine())
  _    <- IO.eval(println(s"Hello $name!\n"))
} yield ()
```

Before we created the _Function0_ instances ourselves
and wrapped them in the _IO_.

```scala
// IOAppHello.scala
val hello: IO[Unit] = for {
  _    <- IO(() => print("What's your name?  "))
  name <- IO(() => scala.io.StdIn.readLine())
  _    <- IO(() => println(s"Hello $name!\n"))
} yield ()
```

## _eval_ and _pure_ (or _succeed_)

These two functions are defined in the _IO_ companion object.
Their implementations are exactly the same, they only
differ in their parameter.

```scala
def eval[A](thunk: => A): IO[A] =
  IO(() => thunk)

def pure[A](a: A): IO[A] =
  IO(() => a)

def succeed[A](a: A): IO[A] =
  pure(a)
```

_pure_ takes a pure value of type _A_ <u>by value</u>.
It is eager and should only be used with pure values as
args, hence the name.

_eval_ takes a computation that returns a value of type
_A_ <u>by name</u>. It is lazy and is intended to be used
with computations returning an _A_. The computation is
executed when the _IO_ is run.

If you passed a computation to _pure_, the computation
would be evaluated eagerly. The result would
be computed before being passed to _pure_. You can do this,
but it's not what _pure_ is intended for.

_IO.succeed_ is just an alias for _IO.pure_

## _raiseError_ (or _fail_)

_pure_ creates an _IO_ which is guaranteed to succeed
(hence the alias _succeed_). _IO.raiseError_ creates
an _IO_ which is guaranteed to fail (hence the alias
_fail_). Just as _pure_ takes a pure value, _raiseError_
take a pure _Throwable_ to indicate failure.

The implementation of _raiseError_ wraps the throwing
of the exception in an _IO_, thus preventing it from being
executed immediately.

```scala
def raiseError[A](exception: Throwable): IO[A] =
  IO(() => throw exception)

def fail[A](exception: Throwable): IO[A] =
  raiseError(exception)
```

_raiseError_ and _fail_ are eager, like _pure_ and
_succeed_.
