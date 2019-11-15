import sbt.Keys._
import sbt._

object Dependencies extends AutoPlugin {
  object autoImport {
    implicit final class DependenciesProject(val project: Project) extends AnyVal {
      def withDependencies: Project = project.settings(defaultDependencySettings)
    }
  }

  val defaultDependencySettings: Seq[Def.Setting[_]] = {

    val scalaTest = Seq(
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
      "org.typelevel" %% "discipline-scalatest" % "1.0.0-RC1"
    )

    Seq(
      libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0",
      libraryDependencies ++= {
        scalaTest
      }.map(_ % Test)
    )
  }
}
