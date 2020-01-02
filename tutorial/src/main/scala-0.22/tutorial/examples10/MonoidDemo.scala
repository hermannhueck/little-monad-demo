package tutorial.examples10

import scala.util.chaining._
import scala.language.implicitConversions
import util._

import tutorial.libMyCats._

// Joiner[Int] in local scope overrides Joiner[Int] in implicit scope (companion object)
val intProductJoiner: Monoid[Int] = new Monoid[Int] {
  override def empty: Int = 1
  override def combine(lhs: Int, rhs: Int): Int =
    lhs * rhs
}

@main def MonoidDemo: Unit = {

  printLineStart()

  s"2 join 3 = ${2 combine 3}" pipe println
  s"2 join 3 = ${2.combine(3)(given intProductJoiner)}" pipe println

  val li1 = List(1, 2, 3)
  val li2 = List(11, 12, 13)
  val liJoined = li1 combine li2

  s"$li1 join $li2 = $liJoined" pipe println

  println

  val sumOfInts = liJoined.combineAll
  s"all ints joined to a product: $sumOfInts" pipe println

  val listOfListOfInts: List[List[Int]] = List(li1, li2, liJoined)
  val joinedLists = listOfListOfInts.combineAll
  s"joined Lists: $joinedLists" pipe println

  println

  s"true join true = ${true combine true}" pipe println
  s"true join false = ${true combine false}" pipe println
  s"false join true = ${false combine true}" pipe println
  s"false join false = ${false combine false}" pipe println

  printLineEnd()
}
