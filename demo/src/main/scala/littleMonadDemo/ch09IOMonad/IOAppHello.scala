package littleMonadDemo.ch09IOMonad

import littleMonadDemo.libMyCats._

object IOAppHello extends util.App {

  // referentially transparent description of the program

  val hello: IO[Unit] = for {
    _    <- IO(() => print("What's your name?  "))
    name <- IO(() => scala.io.StdIn.readLine())
    _    <- IO(() => println(s"Hello $name!\n"))
  } yield ()

  // interpretation / execution of the program
  // is NOT referentially transparent

  hello.unsafeRun()
}
