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
    crossScalaVersions := Seq(dottyVersion, scala2xVersion), // cross compile with Dotty and Scala 2
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

lazy val docs = project // new documentation project
  .in(file("tutorial-docs")) // important: it must not be docs/
  .dependsOn(tutorial)
  .enablePlugins(MdocPlugin)
  .settings(
    scalaVersion := scala2xVersion,
    crossScalaVersions := Seq.empty[String],
    mdocIn := file("tutorial-docs/src"),
    mdocOut := file("tutorial/src/main/scala-2.13/tutorial/docs"),
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
