import ScalacOptions._

val projectName = "little-monad-demo"

val projectDescription = "little demo of monads and functional programming techniques"
val projectVersion     = "0.0.1"

inThisBuild(
  Seq(
    name := projectName,
    description := projectDescription,
    version := projectVersion,
    scalaVersion := "2.13.1",
    publish / skip := true,
    scalacOptions ++= defaultScalacOptions,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "2.0.0"
    ),
    // https://github.com/typelevel/kind-projector
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full),
    // https://github.com/oleg-py/better-monadic-for
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    initialCommands :=
      s"""|
          |import scala.util.chaining._
          |import cats._
          |import cats.implicits._
          |import cats.effect._
          |println
          |""".stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(demo)
  .settings(
    sourceDirectories := Seq.empty
  )

lazy val demo = (project in file("demo"))
  .dependsOn(util)
  .settings(
    name := "demo",
    description := projectDescription
  )

lazy val util = (project in file("util"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "util",
    description := "Utilities",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "build"
  )
