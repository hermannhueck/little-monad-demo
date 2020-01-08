package tutorial.examples09

import tutorial.libMyCats._

import scala.util.chaining._
import util._

object IOAppHello extends App {

  lineStart() pipe println

  // referentially transparent description of the program

  val hello: IO[Unit] = for {
    _    <- IO(() => print("What's your name?  "))
    name <- IO(() => scala.io.StdIn.readLine())
    _    <- IO(() => println(s"Hello $name!\n"))
  } yield ()

  // interpretation / execution of the program
  // is NOT referentially transparent

  hello.unsafeRun()

  lineEnd() pipe println
}
