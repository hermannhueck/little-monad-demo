package tutorial.examples01To07

import tutorial.libMyCats._

import util._
import scala.util.chaining._

@main def Example03: Unit =

  line().green pipe println

  println("----- Identity:")

  val id: Id[Int] = 42
  val idResult = compute(id, id)
  println(idResult)

  line().green pipe println
