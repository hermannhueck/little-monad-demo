package tutorial.examples09

import scala.util.chaining.*
import util.*

@main def HelloWithFunction0: Unit = {

  line().green pipe println

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

  line().green pipe println
}
