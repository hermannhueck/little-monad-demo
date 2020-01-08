package referentialTransparency

import cats.effect.IO

import scala.util.chaining._
import util._

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransparent extends App {

  lineStart() pipe println

  def putStrLn(line: String): IO[Unit] =
    IO { println(line) }

  def func(ioa1: IO[Unit], ioa2: IO[Unit]): IO[Unit] =
    for {
      _ <- ioa1
      _ <- ioa2
    } yield ()

  func(putStrLn("hi"), putStrLn("hi")).unsafeRunSync() // prints "hi" twice
  //=> hi
  //=> hi

  println("-----")

  val x: IO[Unit] = putStrLn("hi")
  func(x, x).unsafeRunSync() // prints "hi" twice
  //=> hi
  //=> hi

  // 'func' IS referentially transparent!

  lineEnd() pipe println
}
