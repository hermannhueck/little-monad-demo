package tutorial.examples08

final case class Database(
    users: Map[Int, String],
    passwords: Map[Int, String]
)

object Database {

  private val users: Map[Int, String] = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )

  private val passwords: Map[Int, String] = Map(
    1 -> "zerocool",
    2 -> "acidburn",
    3 -> "secret"
  )

  private val db: Database = Database(users, passwords)

  def get: Database = db
}
