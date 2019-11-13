// Code quality
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.7")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.8")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.2.1")
// vulnerabilities
addSbtPlugin("net.vonbuchholtz" % "sbt-dependency-check" % "1.3.3")
// compiler
addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.3.4")
// release publishing
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.12")
