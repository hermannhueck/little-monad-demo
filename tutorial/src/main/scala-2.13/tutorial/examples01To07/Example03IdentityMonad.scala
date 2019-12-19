package tutorial.examples01To07;

import scala.util.chaining._
import util._

import tutorial.libMyCats._

object Example03IdentityMonad extends App {

  lineStart() pipe println

  println("----- Identity:")

  val idResult = compute(42, 42)
  println(idResult)

  lineEnd() pipe println
}
