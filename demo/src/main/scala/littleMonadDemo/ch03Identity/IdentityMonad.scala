package littleMonadDemo.ch03Identity

import littleMonadDemo.libCompute.LibComputeWithMyCats._

object IdentityMonad extends util.App {

  println("----- Identity:")

  val idResult = compute(42, 42)
  println(idResult)
}
