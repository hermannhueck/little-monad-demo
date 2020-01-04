package tutorial.examples01To07

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

import scala.util.chaining._
import util._

object Example01ForComprehensions extends App {

  lineStart() pipe println

  def compiteInts(list1: List[Int], list2: List[Int]): List[(Int, Int)] =
    for {
      i1 <- list1
      i2 <- list2
    } yield (i1, i2)

  def compiteInts2(list1: List[Int], list2: List[Int]): List[(Int, Int)] =
    list1.flatMap { i1 =>
      list2.map { i2 =>
        (i1, i2)
      }
    }

  val l1 = List(1, 2, 3)
  val l2 = List(1, 20, 30, 40)

  val lResult = compiteInts(l1, l2)
  println(lResult)

  val lResult2 = compiteInts2(l1, l2)
  println(lResult2)

  def compiteInts(option1: Option[Int], option2: Option[Int]): Option[(Int, Int)] =
    for {
      i1 <- option1
      i2 <- option2
    } yield (i1, i2)

  val o1 = Some(1)
  val o2 = Some(10)

  val oResult = compiteInts(o1, o2)
  println(oResult)

  def compiteInts(future1: Future[Int], future2: Future[Int]): Future[(Int, Int)] =
    for {
      i1 <- future1
      i2 <- future2
    } yield (i1, i2)

  val f1 = Future(1)
  val f2 = Future(10)

  val fResult = compiteInts(f1, f2)
  val res     = Await.result(fResult, 3.seconds)
  println(res)

  lineEnd() pipe println
}
