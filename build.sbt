import Dependencies._

// For parity with non/spire
lazy val spireVersion = "0.14.1"
lazy val scalaTestVersion = "3.0.0"

lazy val commonSettings = inThisBuild(Seq(
  scalaOrganization := "org.typelevel", // provide literal types
  scalaVersion := "2.12.3-bin-typelevel-4",
  version      := "0.1.0-SNAPSHOT",
  libraryDependencies += "org.typelevel" %% "spire" % spireVersion,
  scalacOptions ++= Seq(
    "-Yliteral-types",
    "-Ypartial-unification",
    "-language:higherKinds"
  ))
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "spire-matrix",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    ),
    initialCommands := """
      import spire.std.double._
      import spire.algebra.VectorSpace
      import spire.syntax.vectorSpace._
    """
  )

lazy val blas = (project in file("blas")).
  settings(commonSettings : _*).
  settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "net.sourceforge.f2j" % "arpack_combined_all" % "0.1",
      "com.github.fommil.netlib" % "all" % "1.1.2" // "core" will not pull any blas implemenations.
    ),
    initialCommands += """
        import blas._
        import com.github.fommil.netlib._

        implicit val blasInstance : BLAS = new F2jBLAS()
    """
  ).dependsOn(core)

lazy val cats = (project in file("cats")).
  settings(
    name := "Cats interop for Matrices for Spire",
    libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-MF"
  ).
  dependsOn(core)
