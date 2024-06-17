import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  private val playVersion = "play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"        %% s"bootstrap-backend-$playVersion"  % bootstrapVersion,
    "org.typelevel"      %% "cats-core"                        % "2.12.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %%  s"bootstrap-test-$playVersion"  % bootstrapVersion  % Test,
    "org.mockito"             %   "mockito-inline"                % "5.2.0"          % Test
  )

  val it: Seq[ModuleID] = Seq.empty

  def apply(): Seq[ModuleID] = compile ++ test ++ it

}
