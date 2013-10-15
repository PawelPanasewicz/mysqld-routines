logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "[1.5.,)")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "[0.7.,)")