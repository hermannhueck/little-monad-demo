package tutorial.examples10To12

import scala.util.chaining._
import scala.language.implicitConversions
import util._

import tutorial.libMyCats._
// should work w/o this import
import tutorial.libMyCats.Semigroup.given

@main def Example11MonoidInstances: Unit = {

  line().green pipe println

  s"${line(10)} Semigroup[Boolean] ${line(40)}".green pipe println

  s"true combine true = ${true combine true}" pipe println
  s"true combine false = ${true combine false}" pipe println
  s"false combine true = ${false combine true}" pipe println
  s"false combine false = ${false combine false}" pipe println

  s"${line(10)} Semigroup[Option[A]] ${line(40)}".green pipe println
  
  val optI1 = Option(15)
  val optI2 = Option(27)
  val none = Option.empty[Int]
  s"none combine none = ${none combine none}" pipe println
  s"$optI1 combine none = ${optI1 combine none}" pipe println
  s"none combine $optI1 = ${none combine optI2}" pipe println
  s"$optI1 combine $optI2 = ${optI1 combine optI2}" pipe println

  s"${line(10)} Monoid[Option[A]] ${line(40)}".green pipe println

  val joinedOptions1 = List(Option(12), Option(14), Option(16)).combineAll
  s"joined Options #1: $joinedOptions1" pipe println
  val joinedOptions2 = List(none, Option(14), Option(16)).combineAll
  s"joined Options #2: $joinedOptions2" pipe println

  s"${line(10)} Semigroup[Map[K, V]] ${line(40)}".green pipe println

  val mapSI1 = Map[String, Int]("x" -> 1, "z" -> 10)
  val mapSI2 = Map[String, Int]("y" -> 2, "z" -> 12)
  val mapsJoined = mapSI1 combine mapSI2
  s"$mapSI1 combine $mapSI2 = $mapsJoined" pipe println
  
  s"${line(10)} Monoid[Map[K, V]] ${line(40)}".green pipe println

  val mapSI3 = Map[String, Int]("w" -> 3, "z" -> 20)
  val allMapsJoined =
    List(mapSI1, mapSI2, mapSI3)
    .combineAll
    .ensuring { resultMap => resultMap("z") == 42 }
  s"all maps joined: $allMapsJoined" pipe println

  val mapIS1 = Map(1 -> "Monoids", 3 -> "really", 4 -> "awesome")
  val mapIS2 = Map(2 -> "are", 4 -> ".")
  val sentence =
    (mapIS1 combine mapIS2)
      .toList
      .sorted
      .unzip
      ._2
      .mkString(" ")
  sentence pipe println

  line().green pipe println
}
