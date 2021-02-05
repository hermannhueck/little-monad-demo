package tutorial.examples01To07

import scala.util.chaining._
import util._

@main def Example06FunctionsAreValues: Unit = {

  line().green pipe println

  val plus1: Function1[Int, Int] = x => x + 1
  def plus2(x: Int): Int         = x + 2
  val plus2Func: Int => Int      = x => plus2(x) // explicit eta expansion
  def plus3(x: Int): Int         = x + 3 // implicit eta expansion at call site
  val double: Int => Int         = _ * 2

  val lf1a: List[Int => Int] = List(x => x + 1, x => x + 2, x => x + 3)
  val lf1b: List[Int => Int] = List(_ + 1, _ + 2, _ + 3)
  // with eta expansion for plus3 the compiler converts a method into a function.
  val lf1c: List[Int => Int] = List(plus1, plus2Func, plus3)
  // lf1c pipe println

  val li1: List[Int] = lf1c.map(f => f.apply(10)) tap println
  val li2: List[Int] = lf1c.map(_.apply(10)) tap println
  val li3: List[Int] = lf1c.map(f => f(10)) tap println
  val li4: List[Int] = lf1c.map(_(10)) tap println

  println

  val lf2a: List[Int => Int] = lf1c map (f => ((x: Int) => double(f(x))))
  val lf2b: List[Int => Int] = lf1c map (f => (x: Int) => f(x) * 2)
  val lf2c: List[Int => Int] = lf1c map (f => f andThen double)
  val lf2d: List[Int => Int] = lf1c map (_ andThen double)

  lf2a.map(_(10)) pipe println
  lf2b.map(_(10)) pipe println
  lf2c.map(_(10)) pipe println
  lf2d.map(_(10)) pipe println

  println

  List(1, 2, 3, 4).fold(0)((x, y) => x + y) pipe println
  List(1, 2, 3, 4).fold(1)((x, y) => x * y) pipe println
  List("a", "b", "c", "d").fold("")((x, y) => x ++ y) pipe println

  val fComposed1: Int => Int = lf1c.fold(identity[Int](_))((f, g) => f andThen g)
  fComposed1(10) pipe println
  val fComposed2: Int => Int = lf2d.fold(identity[Int])((f, g) => f andThen g)
  fComposed2(10) pipe println

  println
  // Eta expansion:

  // times2 is a method
  def times2(x: Int): Int = x * 2

  // explicitly expanding times2 to a function
  List(1, 2, 3) map { x =>
    times2(x)
  } pipe println
  // using the _
  List(1, 2, 3) map { times2(_) } pipe println
  // using the _ appended to the method name
  // List(1, 2, 3) map { times2 _ } pipe println // only Scala 2
  // implicit expansion just using the method name
  List(1, 2, 3) map times2 pipe println

  line().green pipe println
}
