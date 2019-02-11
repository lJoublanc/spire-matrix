// For parity with non/spire
lazy val spireVersion = "0.14.1"
lazy val scalaTestVersion = "3.0.6-SNAP5"

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
  """,
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")
  )
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "Matrices for Spire (core)",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.5.0",
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  )

lazy val blas = (project in file("blas")).
  settings(commonSettings : _*).
  settings(
    name := "Matrices for Spire (BLAS implementation)",
    libraryDependencies ++= Seq(
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
