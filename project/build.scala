import sbt._
import Keys._
import sbtrelease.ReleasePlugin._
import com.typesafe.sbt.SbtScalariform._
import ohnosequences.sbt.SbtS3Resolver.S3Resolver
import ohnosequences.sbt.SbtS3Resolver.{ s3, s3resolver }
import org.scalastyle.sbt.ScalastylePlugin.{ Settings => styleSettings }
import scalariform.formatter.preferences._

object ChaosBuild extends Build {
  lazy val root = Project(
    id = "chaos",
    base = file("."),
    settings = baseSettings ++
               releaseSettings ++
               publishSettings ++
               formatSettings ++
               styleSettings ++
      Seq(
        libraryDependencies ++= Dependencies.root,
        parallelExecution in Test := false,
        fork in Test := true
      )
  )

  lazy val testScalaStyle = taskKey[Unit]("testScalaStyle")

  testScalaStyle := {
    org.scalastyle.sbt.PluginKeys.scalastyle.toTask("").value
  }

  (test in Test) <<= (test in Test) dependsOn testScalaStyle

  lazy val baseSettings = Defaults.defaultSettings ++ Seq (
    organization := "mesosphere",
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.6", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6", "-Xlint:unchecked", "-Xlint:deprecation"),
    resolvers ++= Seq(
      "Mesosphere Public Repo"    at "http://downloads.mesosphere.com/maven",
      "Twitter Maven2 Repository" at "http://maven.twttr.com/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )
  )

  lazy val publishSettings = S3Resolver.defaults ++ Seq(
    publishTo := Some(s3resolver.value(
      "Mesosphere Public Repo (S3)",
      s3("downloads.mesosphere.com/maven")
    ))
  )

  lazy val formatSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(IndentWithTabs, false)
      .setPreference(IndentSpaces, 2)
      .setPreference(AlignParameters, true)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(CompactControlReadability, true) //MV: should be false!
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(FormatXml, true)
    )
}

object Dependencies {
  import Dependency._

  val root = Seq(
    // runtime
    guava % "compile",
    guice % "compile",
    guiceServlet % "compile",
    jettyServer % "compile",
    jettyServlet % "compile",
    jettySecurity % "compile",
    jerseyCore % "compile",
    jerseyServer % "compile",
    jerseyServlet % "compile",
    jerseyGuice % "compile",
    jacksonScala % "compile",
    jacksonJaxrs % "compile",
    hibernate % "compile",
    glassfish % "compile",

    metricsJersey % "compile",
    metricsJvm % "compile",
    metricsJetty % "compile",
    metricsServlets % "compile",

    scallop % "compile",
    mustache % "compile",
    slf4jLog4j % "compile",
    slf4jJul % "compile",
    log4j % "compile",
    liftMD % "compile",

    // test
    Test.junit % "test",
    Test.mockito % "test"
  )
}

object Dependency {
  object V {
    // runtime deps versions
    val Guava = "17.0"
    val Guice = "3.0"
    val Scallop = "0.9.5"
    val Jersey = "1.18.1"
    val Metrics = "3.0.2"
    val Jetty = "8.1.15.v20140411"
    val Jackson = "2.4.1"
    val Hibernate = "5.1.2.Final"
    val Mustache = "0.8.12"
    val Slf4j = "1.7.7"

    // test deps versions
    val JUnit = "4.11"
    val Mockito = "1.9.5"
  }

  val guava = "com.google.guava" % "guava" % V.Guava
  val guice = "com.google.inject" % "guice" % V.Guice
  val guiceServlet = "com.google.inject.extensions" % "guice-servlet" % V.Guice
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % V.Jetty
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % V.Jetty
  val jettySecurity = "org.eclipse.jetty" % "jetty-security" % V.Jetty
  val jerseyCore = "com.sun.jersey" % "jersey-core" % V.Jersey
  val jerseyServer = "com.sun.jersey" % "jersey-server" % V.Jersey
  val jerseyServlet = "com.sun.jersey" % "jersey-servlet" % V.Jersey
  val jerseyGuice = "com.sun.jersey.contribs" % "jersey-guice" % V.Jersey
  val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % V.Jackson
  val jacksonJaxrs = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % V.Jackson
  val hibernate = "org.hibernate" % "hibernate-validator" % V.Hibernate
  val glassfish = "org.glassfish.web" % "javax.el" % "2.2.5"

  val metricsJersey = "com.codahale.metrics" % "metrics-jersey" % V.Metrics
  val metricsJvm = "com.codahale.metrics" % "metrics-jvm" % V.Metrics
  val metricsJetty = "com.codahale.metrics" % "metrics-jetty8" % V.Metrics
  val metricsServlets = "com.codahale.metrics" % "metrics-servlets" % V.Metrics

  val scallop = "org.rogach" %% "scallop" % V.Scallop
  val mustache = "com.github.spullara.mustache.java" % "compiler" % V.Mustache
  val slf4jLog4j = "org.slf4j" % "slf4j-log4j12" % V.Slf4j
  val slf4jJul = "org.slf4j" % "jul-to-slf4j" % V.Slf4j
  val log4j = "log4j" % "log4j" % "1.2.17"
  val liftMD = "net.liftweb" %% "lift-markdown" % "2.6-M4"

  object Test {
    val junit = "junit" % "junit" % V.JUnit
    val mockito = "org.mockito" % "mockito-all" % V.Mockito
  }
}


// vim: set ts=4 sw=4 et:
