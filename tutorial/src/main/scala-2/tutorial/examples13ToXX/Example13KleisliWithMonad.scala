package tutorial.examples13ToXX

import tutorial.libMyCats._

import scala.util.chaining._
import util._

object Example13KleisliWithMonad extends App {

  line().green pipe println

  // Functions: A => F[B], where F is Option in this case
  val s2iOpt: String => Option[Int]     = s => Option(s.toInt)
  val plus2Opt: Int => Option[Int]      = i => Option(i + 2)
  val div10ByOpt: Int => Option[Double] = i => Option(10.0 / i)
  val d2sOpt: Double => Option[String]  = d => Option(d.toString + " !!!")

  s"${line(10)} FlatMapping on the Option context".green pipe println

  val flatMappedOnOpt: String => Option[String] = input =>
    for {
      i1 <- s2iOpt(input)
      i2 <- plus2Opt(i1)
      d  <- div10ByOpt(i2)
      s  <- d2sOpt(d)
    } yield s

  flatMappedOnOpt("3") pipe println // Some(2.0 !!!)

  s"${line(10)} Kleisli Composition defined on Function1".green pipe println

  "andThenF:  ".green pipe print
  (s2iOpt andThenF plus2Opt andThenF div10ByOpt andThenF d2sOpt)("3") pipe println
  "kleisli:   ".green pipe print
  (s2iOpt kleisli plus2Opt kleisli div10ByOpt kleisli d2sOpt)("3") pipe println
  ">=>:       ".green pipe print
  (s2iOpt >=> plus2Opt >=> div10ByOpt >=> d2sOpt)("3") pipe println

  "\ncomposeF:  ".green pipe print
  (d2sOpt composeF div10ByOpt composeF plus2Opt composeF s2iOpt)("3") pipe println
  "cokleisli: ".green pipe print
  (d2sOpt cokleisli div10ByOpt cokleisli plus2Opt cokleisli s2iOpt)("3") pipe println
  "<=<:       ".green pipe print
  (d2sOpt <=< div10ByOpt <=< plus2Opt <=< s2iOpt)("3") pipe println

  line().green pipe println
}
