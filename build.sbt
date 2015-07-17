name := "rs-root"

lazy val root = project.in(file(".")).
  aggregate(rsJs, rsJvm)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val rs = crossProject.in(file("."))
  .settings(
    organization := "tw",
    name := "reactive-streams",
    scalaVersion := "2.11.7",
    version := "0.1-SNAPSHOT",
    transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
    libraryDependencies += "me.chrons" %%% "boopickle" % "1.0.0",
    libraryDependencies += "com.softwaremill.macwire" %% "macros" % "1.0.5"
  )
  .jvmSettings(Revolver.settings: _*)
  .jvmSettings(
    fork := true,
    libraryDependencies ++= Dependencies.jvmLibs,
    mainClass in Revolver.reStart := Some("Main")
  )
  .jsSettings(
//    persistLauncher in Compile := true,
//    persistLauncher in Test := false,
    scalaJSStage in Global := FastOptStage,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.1",
      "org.monifu" %%% "monifu" % "1.0-M2"
    )
  )

lazy val rsJvm = rs.jvm.settings(
  (resourceGenerators in Compile) <+=
    (fastOptJS in Compile in rsJs, packageScalaJSLauncher in Compile in rsJs).map((f1, f2) => Seq(f1.data, f2.data)),
  watchSources <++= (watchSources in rsJs)
)

lazy val rsJs = rs.js
