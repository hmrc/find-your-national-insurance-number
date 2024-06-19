resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("uk.gov.hmrc"        %  "sbt-auto-build"         % "3.22.0")
addSbtPlugin("uk.gov.hmrc"        %  "sbt-distributables"     % "2.5.0")
addSbtPlugin("org.playframework"  %  "sbt-plugin"             % "3.0.3")
addSbtPlugin("org.scoverage"      %  "sbt-scoverage"          % "2.0.9")
