# Chapter 02:
# Monadic *compute*

To abstract over the effect types (*List*, *Option*,
*Future*) we we will use a Monad. A Monad provides us
(amongst other operators) *flatMap* and *map*. It allows
us to implement 