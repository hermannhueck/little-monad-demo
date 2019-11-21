package littleMonadDemo.ch09IOMonad

import littleMonadDemo.libMyCats._

object IOAppHelloWithEval extends util.App {

  // referentially transparent description of the program

  val hello: IO[Unit] = for {
    _    <- IO.eval(print("What's your name?  "))
    name <- IO.eval(scala.io.StdIn.readLine())
    _    <- IO.eval(println(s"Hello $name!\n"))
  } yield ()

  // interpretation / execution of the program
  // is NOT referentially transparent

  hello.unsafeRun()
}
