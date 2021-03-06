organization := "com.github.biopet"
organizationName := "Biopet"

startYear := Some(2014)

name := "SamplesTsvToConfig"
biopetUrlName := "samplestsvtoconfig"

biopetIsTool := true

mainClass in assembly := Some(
  "nl.biopet.tools.sampletsvtoconfig.SamplesTsvToConfig")

developers := List(
  Developer(id = "ffinfo",
            name = "Peter van 't Hof",
            email = "pjrvanthof@gmail.com",
            url = url("https://github.com/ffinfo"))
)

scalaVersion := "2.11.12"

libraryDependencies += "com.github.biopet" %% "tool-utils" % "0.6"
libraryDependencies += "com.github.biopet" %% "common-utils" % "0.8"
libraryDependencies += "com.github.biopet" %% "tool-test-utils" % "0.3"
