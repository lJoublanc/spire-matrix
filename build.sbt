// For parity with non/spire
lazy val spireVersion = "0.14.1"
lazy val scalaTestVersion = "3.0.0"

lazy val commonSettings = inThisBuild(Seq(
  scalaOrganization := "org.typelevel", // provide literal types
  scalaVersion := "2.12.4-bin-typelevel-4",
  version      := "0.1.0-SNAPSHOT",
  libraryDependencies += "org.typelevel" %% "spire" % spireVersion,
  scalacOptions ++= Seq(
    "-Yliteral-types",
    "-Ypartial-unification",
    "-language:higherKinds",
    "-feature",
    "-Ywarn-unused:imports",
    "-Xlint"
  ),
  initialCommands in consoleQuick := """
    import spire.std.double._
    import spire.algebra.VectorSpace
    import spire.syntax.vectorSpace._
  """
  )
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "Matrices for Spire (core)",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )

lazy val blas = (project in file("blas")).
  settings(commonSettings : _*).
  settings(
    name := "Matrices for Spire (BLAS implementation)",
    libraryDependencies ++= Seq(
      compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
      "net.java.dev.jna" % "jna" % "4.5.2",
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    ),
    scalacOptions in (Compile, console) ~= {
      _.filterNot("-Ywarn-unused:imports" == _)
       .filterNot("-Xlint" == _)
    },
    initialCommands in console += """
      import spire.std.double._
      import spire.algebra.VectorSpace
      import spire.syntax.vectorSpace._
      import spire.math.matrix._
      import spire.blas.implicits._
    """
  ).dependsOn(core)

lazy val fs2 = (project in file("fs2")).
  settings(
    name := "Matrices for Spire (fs2 implementation)",
    libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.0-M8" 
  ).
  dependsOn(core)
