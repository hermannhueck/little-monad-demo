package tutorial.examples10To12

import scala.util.chaining._
import scala.language.implicitConversions
import util._

import tutorial.libMyCats._

@main def Example12Function1MonoidInstances: Unit = {

  line().green pipe println

  def plus2(x: Int): Int = x + 2
  def times2(x: Int): Int = x * 2
  def minus10(x: Int): Int = x - 10

  val lii: List[Int => Int] = List(plus2, times2, minus10)
  
  val fComposedWithAndThen1 = Monoid[Int => Int].combineAll(lii)
  val fComposedWithAndThen2 = lii.combineAll

  fComposedWithAndThen1(5).ensuring(_ == 4) pipe println
  fComposedWithAndThen2(5).ensuring(_ == 4) pipe println

  println
  import Monoid.function1ComposeMonoid
  
  val fComposedWithCompose1 = function1ComposeMonoid.combineAll(lii)
  val fComposedWithCompose2 = lii.combineAll(given function1ComposeMonoid)

  fComposedWithCompose1(5).ensuring(_ == -8) pipe println
  fComposedWithCompose2(5).ensuring(_ == -8) pipe println

  println
  import Monoid.function1ApplyMonoid

  val lis: List[Int => String] = lii.map(f => f andThen (x => s"${x.toString} "))
  val fComposedWithApply = lis.combineAll(given function1ApplyMonoid)
  fComposedWithApply(5).trim pipe println

  line().green pipe println
}