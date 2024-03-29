scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.2.1"

lazy val scalaSettings = Seq(
  scalaVersion := "2.13.0",
  scalacOptions ++= Seq(
    "-Yrangepos", // required by SemanticDB compiler plugin
    "-Ywarn-unused" // required for RemoveUnused
  )
)

lazy val commonSettings = Seq(
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  addCompilerPlugin(scalafixSemanticdb),
  bloopExportJarClassifiers in Global := Some(Set("sources"))
)

lazy val testSettings = Seq(
  logBuffered in Test := false,
  parallelExecution in Test := false,
  testOptions in Test += Tests.Argument("-oDF")
)

lazy val root = project
  .in(file("."))
  .settings(
    scalaSettings,
    console := (console in (core, Compile)).value,
    console in Test := (console in (core, Test)).value
  )
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .settings(
    moduleName := "jurassic-park-service",
    name := moduleName.value,
    scalaSettings,
    commonSettings,
    testSettings
  )
  .withDependencies

def addCommandsAlias(name: String, values: List[String]) =
  addCommandAlias(name, values.mkString(";", ";", ""))

addCommandsAlias(
  "tidy",
  List(
    "scalafix RemoveUnused",
    "scalafix SortImports"
  )
)

addCommandsAlias(
  "validate",
  List(
    "+clean",
    "+test",
    "scalafmtCheck",
    "scalafmtSbtCheck"
  )
)
