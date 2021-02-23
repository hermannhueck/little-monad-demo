package tutorial.examples10To12

import scala.util.chaining._
import util._

import tutorial.libMyCats._

object Example10MonoidDemo extends App {

  // Monoid[Int] in local scope overrides Monoid[Int] in implicit scope (companion object)
  val intProductMonoid: Monoid[Int] = new Monoid[Int] {
    override def empty: Int                       = 1
    override def combine(lhs: Int, rhs: Int): Int =
      lhs * rhs
  }

  line().green pipe println

  s"${line(10)} Semigroup[Int] ${line(40)}".green pipe println

  s"2 combine 3 = ${2 combine 3}" pipe println
  s"2 combine 3 = ${2.combine(3)(intProductMonoid)}" pipe println

  s"${line(10)} Semigroup[List[Int]] ${line(40)}".green pipe println

  val li1      = List(1, 2, 3)
  val li2      = List(11, 12, 13)
  val liJoined = li1 combine li2

  s"$li1 combine $li2 = $liJoined" pipe println

  s"${line(10)} Monoid[Int] ${line(40)}".green pipe println

  val sumOfInts     = liJoined.combineAll
  val productOfInts = liJoined.combineAll(intProductMonoid)
  s"all ints joined to a sum: $sumOfInts" pipe println
  s"all ints joined to a product: $productOfInts" pipe println

  s"${line(10)} Monoid[List[Int]] ${line(40)}".green pipe println

  val listOfListOfInts: List[List[Int]] = List(li1, li2, liJoined)
  val joinedLists                       = listOfListOfInts.combineAll
  s"joined Lists: $joinedLists" pipe println

  line().green pipe println
}
