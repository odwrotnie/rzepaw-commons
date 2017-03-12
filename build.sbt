name := "rzepaw-commons"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

val slickVersion = "3.2.0"

lazy val configuration = RootProject(uri("https://github.com/odwrotnie/configuration.git"))
lazy val rzepawCommons = project.in(file(".")).dependsOn(configuration)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"


//libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.7"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

libraryDependencies += "de.jollyday" % "jollyday" % "0.5.1"

libraryDependencies += "net._01001111" % "jlorem" % "1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "org.ocpsoft.prettytime" % "prettytime" % "4.0.1.Final"

libraryDependencies += "io.github.cloudify" %% "spdf" % "1.3.1"

//libraryDependencies += "org.scala-lang" % "scala-actors" % "2.11.7"

libraryDependencies += "org.apache.commons" % "commons-email" % "1.3.3"

libraryDependencies += "commons-net" % "commons-net" % "3.3"

libraryDependencies += "com.github.lalyos" % "jfiglet" % "0.0.7"

libraryDependencies += "org.apache.poi" % "poi" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi-ooxml-schemas" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi-excelant" % "3.10-FINAL"

libraryDependencies += "org.apache.poi" % "poi-scratchpad" % "3.10-FINAL"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.6"

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

libraryDependencies += "org.clapper" %% "classutil" % "1.0.11"

// SLICKY

libraryDependencies += "com.typesafe.slick" %% "slick" % slickVersion

//libraryDependencies += "com.typesafe.slick" %% "slick-extensions" % "3.1.0"

libraryDependencies += "com.wix" %% "accord-core" % "0.5"

libraryDependencies += "com.h2database" % "h2" % "1.3.175"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.40"

libraryDependencies += "io.reactivex" % "rxjava-reactive-streams" % "1.0.1"

libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % slickVersion


// SPRAY

val SPRAY_VERSION = "1.3.1"

val SPRAY_JSON_VERSION = "1.2.6"

val AKKA_VERSION = "2.4.9"

val AKKA_HTTP_VERSION = "10.0.0"

libraryDependencies += "io.spray" %% "spray-client" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-io" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-util" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-can" % SPRAY_VERSION

libraryDependencies += "io.spray" %%  "spray-json" % SPRAY_JSON_VERSION

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AKKA_VERSION

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % AKKA_HTTP_VERSION

libraryDependencies += "com.typesafe.akka" %% "akka-http-xml" % AKKA_HTTP_VERSION

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.2"

libraryDependencies += "org.pegdown" % "pegdown" % "1.6.0"

libraryDependencies += "com.github.jsqlparser" % "jsqlparser" % "0.9.6"
