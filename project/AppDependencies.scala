import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.5.0"
  private val playVersion = "play-30"
  private val httpVerbsVersion = "15.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-backend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc" %% s"http-verbs-$playVersion" % httpVerbsVersion,
    "org.typelevel" %% "cats-core" % "2.13.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootstrapVersion % Test,
    "uk.gov.hmrc" %% s"http-verbs-test-$playVersion" % httpVerbsVersion,
    "org.mockito" % "mockito-inline" % "5.2.0" % Test
  )

  val it: Seq[ModuleID] = Seq.empty

  def apply(): Seq[ModuleID] = compile ++ test ++ it

}
