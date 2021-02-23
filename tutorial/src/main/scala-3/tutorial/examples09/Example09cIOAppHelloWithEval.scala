package tutorial.examples09

import tutorial.libMyCats.*

import scala.util.chaining.*
import util.*

@main def IOAppHelloWithEval: Unit = {

  line().green pipe println

  // referentially transparent description of the program

  val helloDesc: IO[Unit] =
    for
      _    <- IO.eval(print("What's your name?  "))
      name <- IO.eval(scala.io.StdIn.readLine())
      _    <- IO.eval(println(s"Hello $name!\n"))
    yield ()

  // interpretation / execution of the program
  // is NOT referentially transparent

  helloDesc.unsafeRun()

  line().green pipe println
}
