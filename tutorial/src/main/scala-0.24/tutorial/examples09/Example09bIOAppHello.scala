package tutorial.examples09

import tutorial.libMyCats._

import scala.util.chaining._
import scala.language.implicitConversions
import util._

@main def IOAppHello: Unit = {

  line().green pipe println

  // referentially transparent description of the program

  val helloDesc: IO[Unit] =
    for
      _    <- IO(() => print("What's your name?  "))
      name <- IO(() => scala.io.StdIn.readLine())
      _    <- IO(() => println(s"Hello $name!\n"))
    yield ()

  // interpretation / execution of the program
  // is NOT referentially transparent

  helloDesc.unsafeRun()

  line().green pipe println
}
