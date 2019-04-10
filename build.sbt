name := "spire-matrix"

scalaVersion := "2.13.0-M5"

lazy val spireVersion = "0.16.1"

description := "Matrices for Spire (BLAS implementation)"

libraryDependencies ++= Seq(
  "org.typelevel" %% "spire" % spireVersion,
  "net.java.dev.jna" % "jna" % "4.5.2",
  "com.github.mpilquist" %% "simulacrum" % "0.14.0",
  "org.typelevel" %% "spire-laws" % spireVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.6" % "test") //3.0.7 incompat with discipline/laws

enablePlugins(GitVersioning)

scalacOptions ++= Seq(
  "-language:higherKinds",
  "-language:reflectiveCalls",
  "-language:implicitConversions",
  "-feature",
  "-Ywarn-unused:imports",
  "-Xlint",
  "-Ymacro-annotations"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

scalacOptions in (Compile, console) ~= {
  _.filterNot("-Ywarn-unused:imports" == _)
   .filterNot("-Xlint" == _)
}

initialCommands in console += """
    import spire.std.double._
    import spire.algebra.VectorSpace
    import spire.syntax.vectorSpace._
    import spire.blas._
  """

initialCommands in consoleQuick := """
  import spire.std.double._
  import spire.algebra.VectorSpace
  import spire.syntax.vectorSpace._
"""
