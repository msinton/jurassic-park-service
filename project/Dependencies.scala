import sbt.Keys._
import sbt._

object Dependencies extends AutoPlugin {
  object autoImport {
    implicit final class DependenciesProject(val project: Project) extends AnyVal {
      def withDependencies: Project = project.settings(defaultDependencySettings)
    }
  }

  val defaultDependencySettings: Seq[Def.Setting[_]] = {

    val catsVersion = "2.0.0"

    val http4s = Seq(
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-blaze-client",
      "org.http4s" %% "http4s-circe"
    ).map(_ % "0.21.0-M4")

    val kittens = Seq(
      "org.typelevel" %% "cats-free" % catsVersion, // TODO learn free
      "org.typelevel" %% "kittens" % catsVersion
    )

    val logging = Seq(
      "io.chrisdavenport" %% "log4cats-slf4j" % "0.4.0-M2"
    )

    val refined = Seq(
      "eu.timepit" %% "refined",
      "eu.timepit" %% "refined-cats"
    ).map(_ % "0.9.10")

    val circe = Seq(
      "io.circe" %% "circe-generic"
    ).map(_ % "0.12.0-M4")

    val enumeratum = Seq(
      "com.beachape" %% "enumeratum" % "1.5.13",
      "com.beachape" %% "enumeratum-circe" % "1.5.21",
      "com.beachape" %% "enumeratum-cats" % "1.5.16"
    )

    val scalaTest = Seq(
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
      "org.typelevel" %% "discipline-scalatest" % "1.0.0-RC1"
      // "org.typelevel" %% "cats-testkit" % catsVersion, // TODO what are these for?
      // "org.slf4j" % "slf4j-nop" % "1.7.29"
    )

    Seq(
      libraryDependencies ++= {
        http4s ++
          kittens ++
          logging ++
          refined ++
          circe ++
          enumeratum
      },
      libraryDependencies ++= {
        scalaTest
      }.map(_ % Test)
    )
  }
}
