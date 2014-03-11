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

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.0"

libraryDependencies += "com.github.tminglei" % "slick-pg_2.10" % "0.5.1.3"

libraryDependencies += "com.github.tminglei" % "slick-pg_play-json_2.10" % "0.5.1.3"

libraryDependencies += "org.postgresql" % "postgresql" % "9.3-1100-jdbc41"