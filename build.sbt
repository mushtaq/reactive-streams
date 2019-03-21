name := "reactive-streams"

version := "1.0"

scalaVersion := "2.12.8"

transitiveClassifiers in Global := Seq(Artifact.SourceClassifier)

updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies := Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  //test
  "org.scalatest" %% "scalatest" % "3.0.6" % "test",
)
