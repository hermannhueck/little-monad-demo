package middlemax

import scala.util.chaining._
import util._

object MiddleMax extends App {

  lineStart() pipe println

  def middlemax[T: Ordering](xs: List[T]): List[T] = {

    val sorted = xs.sorted.reverse

    val (middle, listEvenLength) =
      if (sorted.length % 2 == 0)
        (List.empty[T], sorted)
      else
        (List(sorted.head), sorted.tail)

    val (l1, l2) = listEvenLength
      .grouped(2) // Iterator[List[T]] each List with 2 elems
      .toList     // List[List[T]] each List with 2 elems
      .map {
        case List(x, y) => (x, y)
        case _          => new UnsupportedOperationException("should never happen")
      }
      .asInstanceOf[List[(T, T)]] // List[(T, T)]
      .unzip // (List[T], List[T])
      .swap  // (List[T], List[T])

    l1.sorted ++ middle ++ l2
  }

  implicit class ListSyntax[T: Ordering](private val xs: List[T]) {
    @inline def mimax: List[T] = middlemax(xs)
  }

  List(1, 2, 3, 4, 5, 6).mimax.ensuring {
    _ == List(1, 3, 5, 6, 4, 2)
  } pipe println

  List(1, 2, 3, 4, 5).mimax.ensuring {
    _ == List(1, 3, 5, 4, 2)
  } pipe println

  lineEnd() pipe println
}
