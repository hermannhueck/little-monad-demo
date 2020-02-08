package tutorial.examples09

import scala.util.chaining._
import scala.language.implicitConversions
import util._

@main def HelloWithFunction0: Unit = {

  lineStart() pipe println

  val promptForName: () => Unit =
    () => print("Whats your name?  ")

  val readInput: () => String =
    () => scala.io.StdIn.readLine()

  def printHello(name: String): () => Unit =
    () => println(s"Hello $name!")

  promptForName()
  val name = readInput()

  val printHelloWithName: () => Unit =
    printHello(name)
  printHelloWithName()

  lineEnd() pipe println
}
