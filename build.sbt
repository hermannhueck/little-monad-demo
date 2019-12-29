import ScalacOptions._

val projectName = "little-monad-tutorial"

val projectDescription = "Little Monad Tutorial in Scala"
val projectVersion     = "0.0.1"

// val dottyVersion = "0.21.0-RC1"
val dottyVersion   = dottyLatestNightlyBuild.get
val scala2xVersion = "2.13.1"

def dependenciesFor(deps: Seq[ModuleID], scalaVersion: String): Seq[ModuleID] = {
  deps.map(_.withDottyCompat(scalaVersion)) ++
    Seq("com.novocode" % "junit-interface" % "0.11" % Test) ++ {
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, _)) => // Scala 2.x
        Seq(
          "org.typelevel" %% "cats-effect" % "2.0.0",
          compilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full)
        )
      case _ => // Scala 0.x == Dotty
        Seq.empty
    }
  }
}

def scalacOptionsFor(scalaVersion: String): Seq[String] = {
  println(s"\n>>>>>          compiling for Scala $scalaVersion\n")
  if (scalaVersion.startsWith("0."))
    defaultDotcOptions
  else
    defaultScalacOptions
}

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
    libraryDependencies := dependenciesFor(libraryDependencies.value, scalaVersion.value),
  )
