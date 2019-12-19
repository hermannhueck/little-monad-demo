package tutorial.examples09

import scala.util.chaining._
import util._

object HelloWithFunction0 extends App {

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
