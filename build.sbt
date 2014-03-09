scalaVersion := "2.10.3"

organization := "retrieve"

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "io.spray" % "spray-client" % "1.2.0"

libraryDependencies += "io.spray" %%  "spray-json" % "1.2.5"

libraryDependencies += "com.typesafe" % "config" % "1.2.0"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0.5"

libraryDependencies +=  "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.0"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"

