import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaOrganization := "org.typelevel", // provide literal types
      scalaVersion := "2.12.3-bin-typelevel-4",
      version      := "0.1.0-SNAPSHOT",
      scalacOptions += "-Yliteral-types"
    )),
    name := "BayesTrade Demo",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "spire" % "0.14.1"
    )
  )
