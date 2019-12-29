import ScalacOptions._
import Dependencies._

val projectName        = "little-monad-tutorial"
val projectDescription = "Little Monad Tutorial in Scala (Scala 2 & Dotty)"
val projectVersion     = "0.1.0"

// val dottyVersion = "0.21.0-RC1"
val dottyVersion   = dottyLatestNightlyBuild.get
val scala2xVersion = "2.13.1"

inThisBuild(
  Seq(
    name := projectName,
    description := projectDescription,
    version := projectVersion,
    scalaVersion := dottyVersion,
    // To cross compile with Dotty and Scala 2
    crossScalaVersions := Seq(dottyVersion, scala2xVersion),
    publish / skip := true,
    initialCommands :=
      s"""|
          |import scala.util.chaining._
          |println
          |""".stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(tutorial)
  .settings(
    name := projectName,
    description := projectDescription,
    sourceDirectories := Seq.empty
  )

lazy val tutorial = (project in file("tutorial"))
  .settings(
    name := "tutorial",
    description := projectDescription,
    scalacOptions ++= scalacOptionsFor(scalaVersion.value),
    libraryDependencies ++= dependenciesFor(scalaVersion.value),
  )
