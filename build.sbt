lazy val rzepawCommons = (project in file(".")).
  settings(
    name := "rzepaw-commons",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7"
  )


net.virtualvoid.sbt.graph.Plugin.graphSettings


libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.7"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.3"

libraryDependencies += "net._01001111" % "jlorem" % "1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.7"

libraryDependencies += "com.googlecode.sli4j" % "sli4j-slf4j-logback" % "2.0"

libraryDependencies += "org.ocpsoft.prettytime" % "prettytime" % "3.2.7.Final"

libraryDependencies += "io.github.cloudify" %% "spdf" % "1.3.1"

libraryDependencies += "org.scala-lang" % "scala-actors" % "2.11.7"

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

// SLICKY

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.0.0"

libraryDependencies += "com.wix" %% "accord-core" % "0.5"

libraryDependencies += "com.h2database" % "h2" % "1.3.175"

libraryDependencies += "io.reactivex" % "rxjava-reactive-streams" % "1.0.1"

// SPRAY

val SPRAY_VERSION = "1.3.1"

val SPRAY_JSON_VERSION = "1.2.6"

libraryDependencies += "io.spray" %% "spray-client" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-io" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-util" % SPRAY_VERSION

libraryDependencies += "io.spray" %% "spray-can" % SPRAY_VERSION

libraryDependencies += "io.spray" %%  "spray-json" % SPRAY_JSON_VERSION

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.4"
