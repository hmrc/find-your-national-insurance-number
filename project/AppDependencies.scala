import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.2.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-backend-$playVersion"  % bootstrapVersion,
    "org.typelevel"           %% "cats-core"                  % "2.7.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion            % Test
  )

  val it = Seq.empty
}
