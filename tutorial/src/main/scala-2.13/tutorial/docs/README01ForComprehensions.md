# Chapter 01

# for-comprehensions for different effect types

This is the motivating example for the following chapters.

It contains three different _compute_ methods, all
implemented in the same way, but for different effect
types. The effect types are: _List_, _Option_ and _Future_.

```scala
def computeInts(list1: List[Int], list2: List[Int]): List[(Int, Int)] =
  for {
    i1 <- list1
    i2 <- list2
  } yield (i1, i2)
```

Invoked with two _List_'s we get a _List_ of _Int_ pairs as a result.
Analogously for _Option_ and _Future_.

```scala
val l1 = List(1, 2, 3)
// l1: List[Int] = List(1, 2, 3)
val l2 = List(10, 20, 30, 40)
// l2: List[Int] = List(10, 20, 30, 40)

val lResult = computeInts(l1, l2)
// lResult: List[(Int, Int)] = List(
//   (1, 10),
//   (1, 20),
//   (1, 30),
//   (1, 40),
//   (2, 10),
//   (2, 20),
//   (2, 30),
//   (2, 40),
//   (3, 10),
//   (3, 20),
//   (3, 30),
//   (3, 40)
// )
```

Our three varaints of _computeInts_ are boilerplate
as we repeat the same algorithm three times without
changing anything but the effect type.

The example also shows another method, _computeInts2_, which
the desugared version of _computeInts_, but implemented with
_flatMap_ and _map_. _computeInts2_ demonstrates that a
for-comprehension is nothing but syntactic sugar for a
sequence of _flatMap_'s followed by _map_.

```scala
def computeInts2(list1: List[Int], list2: List[Int]): List[(Int, Int)] =
  list1.flatMap { i1 =>
    list2.map { i2 =>
      (i1, i2)
    }
  }
```

In chapter 02 we will implement a compute method which
abstracts over the effect type and will be usable with
_List_, _Option_, _Future_ and any other effect type
which has a Monad instance.
