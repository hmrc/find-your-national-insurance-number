import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "3.3.5"

lazy val appName: String = "find-your-national-insurance-number"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;;models.*;.*(AuthService|BuildInfo|Routes).*",
    ScoverageKeys.coverageMinimumStmtTotal := 78,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val commonScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Werror",
  "-Wconf:msg=unused&src=routes/.*:s",
  "-language:noAutoTupling",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Wconf:msg=Flag.*repeatedly:s"
)

lazy val microservice = Project("find-your-national-insurance-number", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    name := appName,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions ++= commonScalacOptions
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings *)
  .settings(PlayKeys.playDefaultPort := 14022)
  .settings(scoverageSettings *)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    libraryDependencies ++= AppDependencies.test,
    DefaultBuildSettings.itSettings(),
    scalacOptions ++= commonScalacOptions,
  )

addCommandAlias("report", ";clean; coverage; test; it/test; coverageReport")
