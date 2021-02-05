import sbt._
import dotty.tools.sbtplugin.DottyPlugin.autoImport._

object Dependencies {

  val catsEffectVersion    = "2.3.1"
  val scalaTestVersion     = "3.2.3"
  val scalaCheckVersion    = "1.15.2"
  val junitVersion         = "0.11"
  val kindProjectorVersion = "0.11.3"

  lazy val commonDependencies =
    Seq(
      "org.typelevel"  %% "cats-effect" % catsEffectVersion,
      "org.scalatest"  %% "scalatest"   % scalaTestVersion  % Test,
      "org.scalacheck" %% "scalacheck"  % scalaCheckVersion % Test
    )

  lazy val javaDependencies                                =
    Seq("com.novocode" % "junit-interface" % junitVersion % Test)

  def dependenciesFor(scalaVersion: String): Seq[ModuleID] =
    if (scalaVersion.startsWith("3.")) {
      // Dotty dependencies
      commonDependencies.map(_.withDottyCompat(scalaVersion)) ++
        javaDependencies
    } else {
      // Scala 2.x dependencies
      commonDependencies ++
        javaDependencies ++
        Seq(
          compilerPlugin("org.typelevel" % "kind-projector"     % kindProjectorVersion cross CrossVersion.full),
          compilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1")
        )
    }
}
