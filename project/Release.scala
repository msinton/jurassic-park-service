import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport._

object Release extends AutoPlugin {
  object autoImport {
    implicit class ReleaseSettings(val project: Project) extends AnyVal {
      def withRelease: Project = project.settings(releaseSettings)
    }
    implicit class NoPublishSettings(val project: Project) extends AnyVal {
      def withReleaseNoPublish: Project = project.settings(noPublishSettings)
    }
  }

  import ReleaseTransformations._

  lazy val metadataSettings = Seq(
    organization := "com.consideredgames",
    organizationName := "Considered Games Limited",
    organizationHomepage := Some(url("https://consideredgames.com"))
  )

  private lazy val releaseSettings =
    metadataSettings ++ Seq(
      releaseUseGlobalVersion := true,
      releaseTagName := s"${(version in ThisBuild).value}",
      releaseTagComment := s"Releasing ${(version in ThisBuild).value}",
      releaseCommitMessage := s"Setting version to ${(version in ThisBuild).value}",
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := (_ => false),
      //   excludeFilter.in(headerSources) := HiddenFileFilter,
      developers := List(
        Developer(
          id = "msinton",
          name = "Matt Sinton-Hewitt",
          email = "msintonhewitt@gmail.com",
          url = url("http://github.com/msinton")
        )
      ),
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        setReleaseVersion,
        releaseStepCommand("publishLocal"),
        commitReleaseVersion,
        tagRelease,
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    )

  lazy val noPublishSettings =
    releaseSettings ++ Seq(
      skip in publish := true,
      publishArtifact := false
    )
}
