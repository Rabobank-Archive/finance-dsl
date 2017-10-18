// *** Settings ***

useGpg := false

lazy val commonSettings = Seq(
  organization := "nl.rabobank.oss.rules",
  organizationHomepage := Some(url("https://github.com/rabobank-nederland")),
  homepage := Some(url("https://github.com/rabobank-nederland/finance-dsl")),
  version := "0.2.0",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings")
) ++ staticAnalysisSettings ++ publishSettings


// *** Projects ***

lazy val financeDslRoot = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "finance-dsl",
    description := "Finance DSL",
    libraryDependencies ++= dependencies
  )


// *** Dependencies ***

lazy val scalaTestVersion = "2.2.5"

lazy val dependencies = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
)

// *** Static analysis ***

lazy val staticAnalysisSettings = {
  lazy val compileScalastyle = taskKey[Unit]("Runs Scalastyle on production code")
  lazy val testScalastyle = taskKey[Unit]("Runs Scalastyle on test code")

  Seq(
    scalastyleConfig in Compile := (baseDirectory in ThisBuild).value / "project" / "scalastyle-config.xml",
    scalastyleConfig in Test := (baseDirectory in ThisBuild).value / "project" / "scalastyle-test-config.xml",

    // The line below is needed until this issue is fixed: https://github.com/scalastyle/scalastyle-sbt-plugin/issues/44
    scalastyleConfig in scalastyle := (baseDirectory in ThisBuild).value / "project" / "scalastyle-test-config.xml",

    compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
  )
}

addCommandAlias("verify", ";compileScalastyle;testScalastyle;coverage;test;coverageReport;coverageAggregate")
addCommandAlias("verify", ";testScalastyle;test")
addCommandAlias("release", ";clean;compile;publishSigned")


// *** Publishing ***

lazy val publishSettings = Seq(
  pomExtra := pom,
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }
)

lazy val pom =
  <developers>
    <developer>
      <name>Jan-Hendrik Kuperus</name>
      <email>jan-hendrik@scala-rules.org</email>
      <organization>Yoink Development</organization>
      <organizationUrl>http://www.yoink.nl</organizationUrl>
    </developer>
    <developer>
      <name>Jan Ouwens</name>
      <email>jan.ouwens@gmail.com</email>
      <organization>CodeStar</organization>
      <organizationUrl>https://codestar.nl/</organizationUrl>
    </developer>
    <developer>
      <name>Nathan Perdijk</name>
      <email>nathan@scala-rules.org</email>
      <organization>CodeStar</organization>
      <organizationUrl>https://codestar.nl/</organizationUrl>
    </developer>
    <developer>
      <name>Pim Verkerk</name>
      <email>pimverkerk@hotmail.com</email>
      <organization>CodeStar</organization>
      <organizationUrl>https://codestar.nl/</organizationUrl>
    </developer>
    <developer>
      <name>Vincent Zorge</name>
      <email>scala-rules@linuse.nl</email>
      <organization>Linuse</organization>
      <organizationUrl>https://github.com/vzorge</organizationUrl>
    </developer>
  </developers>
  <scm>
    <connection>scm:git:git@github.com:rabobank-nederland/finance-dsl.git</connection>
    <developerConnection>scm:git:git@github.com:rabobank-nederland/finance-dsl.git</developerConnection>
    <url>git@github.com:rabobank-nederland/finance-dsl.git</url>
  </scm>
  
