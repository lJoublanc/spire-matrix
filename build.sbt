import Dependencies._

// For parity with non/spire
lazy val spireVersion = "0.14.1"
lazy val scalaTestVersion = "3.0.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaOrganization := "org.typelevel", // provide literal types
      scalaVersion := "2.12.3-bin-typelevel-4",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions ++= Seq(
        "-Yliteral-types",
        "-Ypartial-unification",
        "-language:higherKinds"
      )
    )),
    name := "Matrices for Spire",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire" % spireVersion,
      "org.typelevel" %% "spire-laws" % spireVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      "net.sourceforge.f2j" % "arpack_combined_all" % "0.1",
      "com.github.fommil.netlib" % "all" % "1.1.2" // "core" will not pull any blas implemenations.
    ),
    initialCommands := """
      import blas._
      import math.show._
      import com.github.fommil.netlib._
      import spire.std.double._
      import spire.algebra.VectorSpace
      import spire.syntax.vectorSpace._
      implicit val blasInstance : BLAS = new F2jBLAS()
    """
  )
