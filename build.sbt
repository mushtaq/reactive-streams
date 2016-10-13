name := "reactive-streams"

version := "1.0"

scalaVersion := "2.11.8"

transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)

updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies := Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",

    //test
    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.11" % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % "2.4.11" % "test"
  )
