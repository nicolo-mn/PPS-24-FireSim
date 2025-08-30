resolvers += "jitpack" at "https://jitpack.io"

inThisBuild(
  List(
    scalaVersion := "3.3.6",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq("-Wunused:all", "-Wunused:imports")
  )
)

lazy val root = (project in file("."))
  .settings(
    name := "PPS-24-FireSim",
    scalafmtOnCompile := true,
    coverageEnabled := true,
    libraryDependencies += "junit" % "junit" % "4.13.2" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    assembly / mainClass := Some("it.unibo.firesim.main"),
  )
