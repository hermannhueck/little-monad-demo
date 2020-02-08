package tutorial.examples01To07

import tutorial.libMyCats._

import util._
import scala.util.chaining._
import scala.language.implicitConversions

@main def Example03: Unit =

  lineStart() pipe println

  println("----- Identity:")

  val id: Id[Int] = 42
  val idResult = compute(id, id)
  println(idResult)

  lineEnd() pipe println

