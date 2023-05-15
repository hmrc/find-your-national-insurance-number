import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.15.0"
  private val hmrcMongoVersion = "1.1.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion            % "test, it",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % hmrcMongoVersion            % Test,
    "com.github.simplyscala"  % "scalatest-embedmongo_2.12"   % "0.2.4"                     % "test",
    "org.mockito" % "mockito-core" % "4.0.0" % "test",
    "org.mockito" %% "mockito-scala" % "1.16.42" % "test",
    "org.mockito" %% "mockito-scala-scalatest" % "1.16.42" % "test"
  )
}
