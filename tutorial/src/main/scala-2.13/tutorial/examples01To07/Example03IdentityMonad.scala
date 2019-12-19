package tutorial.examples01To07;

import tutorial.libMyCats._

object Example03IdentityMonad extends util.App {

  println("----- Identity:")

  val idResult = compute(42, 42)
  println(idResult)
}
