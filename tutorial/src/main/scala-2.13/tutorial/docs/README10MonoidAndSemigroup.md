# Chapter 10

# Monoid and Semigroup

## Semigroup _Joiner_

Let's define an interface for a _Joiner_ which
can join two values of the same type _A_ returning
a joined value of this very type _A_.

```scala
trait Joiner[A] {
  def join(lhs: A, rhs: A): A
}
```

As long as the type remains an abstract _A_, we cannot
join anything. In order to join _Int_ values with the
operation _+_ we define a concrete _Joiner[Int]_ in the
_Joiner_ companion object.

We also define a _Joiner[List[A]]_ where the joining
operation is implemented as the concatenation of two
values of type _List[A]_ with the _++_ operation. Here
the _A_ remains abstract, because we don't need to
know the element type of the lists to concatenate them.

```scala
object Joiner {

  def apply[A: Joiner]: Joiner[A] = implicitly

  implicit val intJoiner: Joiner[Int] = new Joiner[Int] {
    override def join(lhs: Int, rhs: Int): Int =
      lhs + rhs
  }

  implicit def listJoiner[A]: Joiner[List[A]] = new Joiner[List[A]] {
    override def join(lhs: List[A], rhs: List[A]): List[A] =
      lhs ++ rhs
  }
}
```

We also provided a summoner for _Joiner_ as we did for
the _Monad_ typeclass.

For the syntax extension we provide an implicit class
with an extension method _join_ in order to invoke
join directly as a method of the left value.

```scala
implicit class JoinSyntax[A](lhs: A) {
  def join(rhs: A)(implicit joiner: Joiner[A]): A =
    joiner.join(lhs, rhs)
}
```

With this machinery we can join two _Int_ values
adding them up or two _List_'s concatenating them.

```scala
val intsJoined = 2 join 3
// intsJoined: Int = 5

val li1      = List(1, 2, 3)
// li1: List[Int] = List(1, 2, 3)
val li2      = List(11, 12, 13)
// li2: List[Int] = List(11, 12, 13)
val liJoined = li1 join li2
// liJoined: List[Int] = List(1, 2, 3, 11, 12, 13)
```

This _Joiner_ in fact is a _Semigroup_.

A _Semigroup_ prescibes that the join operation is associative.
This must be considered for each instance you implement.
The _+_ operation for _Int_ values is associative as well as the
_++_ (= concat) operation for _List_'s.


## Monoid Joiner

We're now going to extend our _Joiner_ to make it a Monoid.
We're extending the _Joiner_ with an abstract method _zero_.

_zero_ should give us a neutral value of type _A_, which - joined
to the right or to the left of another value - gives us the other
value.

With this we can define _joinAll_ which takes a of values of
type _A_ and and returns a single _A_, the result of joining
all _A_ values of the _List_. _joinAll_ is defined in terms
of _zero_ and _join_ using the _List#fold_ method.

```scala
trait Joiner[A] {

  def zero: A
  def join(lhs: A, rhs: A): A

  def joinAll(as: List[A]): A =
    as.fold(zero)(join)
}
```

The neutral value of integer addition is _0_,
as _0_ joined with another _Int_ value gives us
the other value.

The neutral value of _List_ concatenation is _List.empty_,
as the empty _List_ joined with another _List_ gives us
the other _List_.

We extend our _Joiner[Int]_ and _Joiner[List[A]]_
instances accordingly.

```scala
object Joiner {

  def apply[A: Joiner]: Joiner[A] = implicitly

  implicit val intJoiner: Joiner[Int] = new Joiner[Int] {
    override def zero: Int = 0
    override def join(lhs: Int, rhs: Int): Int =
      lhs + rhs
  }

  implicit def listJoiner[A]: Joiner[List[A]] = new Joiner[List[A]] {
    override def zero: List[A] = List.empty[A]
    override def join(lhs: List[A], rhs: List[A]): List[A] =
      lhs ++ rhs
  }
}
```

The extension method _join_ (defined in _JoinSyntax_)
allows us to invoke _join_ as a method of the _lhs_ value.

The extension method _joinAll_ (defined in _ListSyntax_)
allows us to invoke _joinAll_ like a _List_ method.

```scala
implicit class JoinSyntax[A](lhs: A) {
  def join(rhs: A)(implicit joiner: Joiner[A]): A =
    joiner.join(lhs, rhs)
}

implicit class ListSyntax[A](as: List[A]) {
  def joinAll(implicit joiner: Joiner[A]): A =
    joiner.joinAll(as)
}
```

Now we can join a _List[List[Int]]_ to a single _List[Int]_,
because we have a _Joiner[List[A]]_ in implicit scope
(i.e. in the _Joiner_ companion object).

And we can join a _List[Int]_ to a single _Int_ value,
because we also have a _Joiner[Int]_ in implicit scope.

```scala
val lists = List(List(1, 2, 3), List(11, 12, 13), List(21, 22, 23))
// lists: List[List[Int]] = List(
//   List(1, 2, 3),
//   List(11, 12, 13),
//   List(21, 22, 23)
// )

// join a List[List[Int]] to a single List[Int]
val ints = lists.joinAll
// ints: List[Int] = List(1, 2, 3, 11, 12, 13, 21, 22, 23)

// joins a List[Int] to a single Int value
val sum = ints.joinAll
// sum: Int = 108
```

This _Joiner_ in fact is a _Monoid_.

_Monoid_ like _Semigroup_ prescibe that the join
operation is associative. Additionally it must define
an empty value (here called _zero_) which abides by the left
identity law and right identity law:

If you combine the empty value to another value
(at the right or at the left hand side) you get the other
value unchanged as a result.

This must be considered for each instance you implement.
_0_ is the empty value of the _+_ operation on _Int_ values.
Nil is the empty value of the _++_ operation on _List_'s.


## The final implementation

To give our _Joiner_ a naming like in _Cats_,
we rename:

- _Joiner_ -> _Monoid_
- _zero_ -> _empty_
- _join_ -> _combine_
- _joinAll_ -> _combineAll_

```scala
trait Monoid[A] {

  def empty: A
  def combine(lhs: A, rhs: A): A

  def combineAll(as: List[A]): A =
    as.fold(empty)(combine)
}
```

Let's factor out _combine_ to the _Semigroup_ trait:

```scala
trait Semigroup[A] {
  def combine(lhs: A, rhs: A): A
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
  def combineAll(as: List[A]): A =
    as.fold(empty)(combine)
}
```

The implementaions can be found in package
_tutorial.libMyCats_.

Now we define the _Semigroup_ instances in the
_Semigroup_ companion object and the _Monoid_
instances in the _Monoid_ companion object:

```scala
object Semigroup {

  def apply[A: Semigroup]: Semigroup[A] = implicitly // summoner


  def instance[A](f: (A, A) => A): Semigroup[A] = new Semigroup[A] {
    override def combine(lhs: A, rhs: A): A = f(lhs, rhs)
  }

  implicit val intSemigroup: Semigroup[Int] =
    instance(_ + _)

  implicit val stringSemigroup: Semigroup[String] =
    instance(_ ++ _)

  // ... other Semigroup instances ...
}

object Monoid {

  def apply[A: Monoid]: Monoid[A] = implicitly // summoner

  def instance[A](z: A)(f: (A, A) => A): Monoid[A] = new Monoid[A] {
    override def empty: A                   = z
    override def combine(lhs: A, rhs: A): A = f(lhs, rhs)
  }

  implicit val intMonoid: Monoid[Int] =
    instance(0)(_ + _)

  implicit val stringMonoid: Monoid[String] =
    instance("")(_ ++ _)

  // ... other Monoid instances ...
}
```

For each instance we factor out the implementation
of _combine_ into a separate trait (_IntSemigroup_
for the _Int_ implemention of _combine_).

Then we can extend this trait to create the _intSemigroup_
instance and again when we create the _intMonoid_ instance.

Thus we do not repeat the implementation of _combine_ twice.

Accordingly for other instances.

See the code for _Semigroup_ and _Monoid_ instances for
- _Int_
- _String_
- _Boolean_
- _List[A]_
- _Option[A]_
- _Map[K, V]_