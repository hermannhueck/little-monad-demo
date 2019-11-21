package littleMonadDemo.ch09IOMonad

object HelloWithFunction0 extends util.App {

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
}
