# Chapter 01

# for-comprehensions for Different Effect Types

This the movivating example for the following chapters.

It contains 3 differnt *compute* methods all implemented
in the same way, but for different effect types. The
effect types are: *List*, *Option* and *Future*.

This is boilerplate as we repeat the the same algorithm
3 times without changing anything but the effect type.

The example also shows another method *compute2* with
the same semantics as *compute* but implemented with
*flatMap* and *map*. *compute2* demonstrates that a
for-comprehension is nothing but syntactic sugar for a
sequence of *flatMap*'s followed by *map*.

In chapter 02 we will implement a compute method which
abstracts over the effect type and will be usable with
*List*, *Option*, *Future* and any other effect type
which has a Monad instance.

To abstract over the effect types (*List*, *Option*,
*Future*) we we will use a Monad. A Monad provides us
(amongst other operators) *flatMap* and *map*. It allows
us to implement 