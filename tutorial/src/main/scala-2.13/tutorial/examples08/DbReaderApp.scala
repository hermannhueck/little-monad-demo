package tutorial.examples08

import scala.util.chaining._
import util._

import tutorial.libMyCats._

// uncomment for use with Cats instead of libMyCats
// import cats._
// import cats.data._
// import cats.implicits._

object DbReaderApp extends App {

  lineStart() pipe println

  val db: Database = Database.get

  def getUserId(name: String): Reader[Database, Option[Int]] =
    Reader { db =>
      db.users
        .map { case (k, v) => (v, k) }
        .get(name)
    }

  def checkUserPassword(userId: Int, password: String): Reader[Database, Boolean] =
    Reader { db =>
      db.passwords
        .get(userId)
        .map(_ == password)
        //.fold(false)(value => value)
        .getOrElse(false)
    }

  def checkLogin(username: String, password: String): Reader[Database, Boolean] =
    for {
      optId     <- getUserId(username)
      id        <- Reader.pure(optId.getOrElse(-1))
      pwCorrect <- checkUserPassword(id, password)
    } yield pwCorrect

  checkLogin("kate", "acidburn").run(db) pipe println
  checkLogin("katy", "acidburn").run(db) pipe println
  checkLogin("margo", "secrets").run(db) pipe println

  lineEnd() pipe println
}
