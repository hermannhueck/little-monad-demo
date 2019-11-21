package tutorial.ch03IdentityMonad

import tutorial.libCompute.WithMyCats._

object Example03 extends util.App {

  println("----- Identity:")

  val idResult = compute(42, 42)
  println(idResult)
}
