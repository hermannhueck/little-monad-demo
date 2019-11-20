# Chapter 01

# for-comprehensions for Different Effect Types

This is the motivating example for the following chapters.

It contains three different _compute_ methods, all implemented
in the same way, but for different effect types. The
effect types are: _List_, _Option_ and _Future_.

This is boilerplate as we repeat the same algorithm
three times without changing anything but the effect type.

The example also shows another method, _compute2_, with
the same semantics as _compute_, but implemented with
_flatMap_ and _map_. _compute2_ demonstrates that a
for-comprehension is nothing but syntactic sugar for a
sequence of _flatMap_'s followed by _map_.

In chapter 02 we will implement a compute method which
abstracts over the effect type and will be usable with
_List_, _Option_, _Future_ and any other effect type
which has a Monad instance.

To abstract over the effect types (_List_, _Option_,
_Future_) we will use a Monad. A Monad provides us
(among other operators) _flatMap_ and _map_. It allows
us to implement
