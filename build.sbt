import ScalacOptions._
import Dependencies._

val projectName        = "little-monad-tutorial"
val projectDescription = "Little Monad Tutorial in Scala (Scala 2 & 3)"
val projectVersion     = "0.2.0"

val scala3Version = "3.0.0-RC1"
// val scala3Version   = dottyLatestNightlyBuild.get
val scala2Version = "2.13.5"

inThisBuild(
  Seq(
    description := projectDescription,
    version := projectVersion,
    scalaVersion := scala3Version,
    crossScalaVersions := Seq(scala3Version, scala2Version), // cross compile with Dotty and Scala 2
    publish / skip := true,
    initialCommands :=
      s"""|
          |import scala.util.chaining._
          |println
          |""".stripMargin                                   // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(tutorial)
  .settings(
    name := projectName,
    description := projectDescription
  )

lazy val tutorial = (project in file("tutorial"))
  .settings(
    name := "tutorial",
    description := projectDescription,
    scalacOptions ++= scalacOptionsFor(scalaVersion.value),
    libraryDependencies ++= dependenciesFor(scalaVersion.value)
  )

lazy val docs = project
  .in(file("tutorial-docs"))
  .dependsOn(tutorial)
  .enablePlugins(MdocPlugin)
  .settings(
    scalaVersion := scala2Version, // mdoc supports Scala 2.x, not Dotty
    crossScalaVersions := Seq.empty[String],
    addCompilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )
  .settings(
    mdocIn := file("tutorial-docs/src"),
    mdocOut := file("tutorial/src/main/scala-2/tutorial/docs"),
    mdocVariables := Map(
      "VERSION" -> version.value
    )
    // mdocExtraArguments := Seq("--clean-target")
    // mdocExtraArguments := Seq("--verbose")
    // mdocExtraArguments := Seq("--clean-target", "--verbose")
  )
