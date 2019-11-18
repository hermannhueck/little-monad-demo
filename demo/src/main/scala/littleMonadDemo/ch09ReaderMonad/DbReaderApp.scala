package littleMonadDemo.ch09ReaderMonad

import scala.util.chaining._

import littleMonadDemo.libMyCats._

// uncomment for use with Cats instead of libMyCats
// import cats._
// import cats.data._
// import cats.implicits._

object DbReaderApp extends util.App {

  val db: Database = Database.get

  def getUserId(name: String): Reader[Database, Option[Int]] =
    Reader { db =>
      db.users
        .map { case (k, v) => (v, k) }
        .get(name)
    }

  def hasUserPassword(userId: Int, password: String): Reader[Database, Boolean] =
    Reader { db =>
      db.passwords
        .get(userId)
        .map(_ == password)
        //.fold(false)(value => value)
        .getOrElse(false)
    }

  def checkLogin(username: String, password: String): Reader[Database, Boolean] =
    for {
      id        <- getUserId(username).map(_.getOrElse(-1))
      pwCorrect <- hasUserPassword(id, password)
    } yield pwCorrect

  checkLogin("kate", "acidburn").run(db) pipe println
  checkLogin("katy", "acidburn").run(db) pipe println
  checkLogin("margo", "secrets").run(db) pipe println
}
