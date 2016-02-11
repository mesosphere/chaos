import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.typesafe.sbt.SbtScalariform._
import ohnosequences.sbt.SbtS3Resolver
import ohnosequences.sbt.SbtS3Resolver.{ S3Resolver, s3, s3resolver }
import org.scalastyle.sbt.ScalastylePlugin.{ Settings => styleSettings }
import spray.revolver.RevolverPlugin.Revolver.{settings => revolverSettings}
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin._

import scalariform.formatter.preferences._

object ChaosBuild extends Build {
  lazy val chaosExamples: Project = Project(
    id = "chaos-examples",
    base = file("chaos-examples"),
    settings = baseSettings ++
      formatSettings ++
      publishSettings
  ).dependsOn(root % "compile->compile; test->test")

  lazy val root = Project(
    id = "chaos",
    base = file("."),
    settings = baseSettings ++
      revolverSettings ++
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
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq("2.11.7"),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"),
    resolvers ++= Seq(
      "Mesosphere Public Repo"    at "http://downloads.mesosphere.io/maven",
      "Twitter Maven2 Repository" at "http://maven.twttr.com/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )
  )

  lazy val publishSettings = S3Resolver.defaults ++ Seq(
    publishTo := Some(s3resolver.value(
      "Mesosphere Public Repo (S3)",
      s3("downloads.mesosphere.io/maven")
    )),
    SbtS3Resolver.s3credentials := new InstanceProfileCredentialsProvider()
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
    liftMD % "compile",

    logback % "compile",
    julToSlf4j % "compile",
    jclOverSlf4j % "compile",
    log4jOverSlf4j % "compile",

    // The macros subproject contains only code which is used at compile-time, hence the provided scope.
    macWireMacros % "provided",
    // The util subproject contains tagging, Wired and the @Module annotation; if you don't use these features, you don't need to include this dependency.
    // macWireUtil % "compile",
    macWireProxy % "compile",

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
    val Metrics = "3.1.2"
    val Jetty = "9.3.2.v20150730"
    val Jackson = "2.6.1"
    val Hibernate = "5.2.1.Final"
    val Mustache = "0.9.0"
    val Logback = "1.1.3"
    val Slf4j = "1.7.12"
    val LiftMarkdown = "2.6.2"
    val Glassfish = "2.2.6"
    val MacWire = "2.2.2"

    // test deps versions
    val JUnit = "4.12"
    val Mockito = "1.10.19"
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
  val glassfish = "org.glassfish.web" % "javax.el" % V.Glassfish

  val metricsJersey = "io.dropwizard.metrics" % "metrics-jersey" % V.Metrics
  val metricsJvm = "io.dropwizard.metrics" % "metrics-jvm" % V.Metrics
  val metricsJetty = "io.dropwizard.metrics" % "metrics-jetty9" % V.Metrics
  val metricsServlets = "io.dropwizard.metrics" % "metrics-servlets" % V.Metrics

  val scallop = "org.rogach" %% "scallop" % V.Scallop
  val mustache = "com.github.spullara.mustache.java" % "compiler" % V.Mustache
  val liftMD = "net.liftweb" %% "lift-markdown" % V.LiftMarkdown

  val logback = "ch.qos.logback" % "logback-classic" % V.Logback
  val log4jOverSlf4j = "org.slf4j" % "log4j-over-slf4j" % V.Slf4j
  val julToSlf4j = "org.slf4j" % "jul-to-slf4j" % V.Slf4j
  val jclOverSlf4j = "org.slf4j" % "jcl-over-slf4j" % V.Slf4j

  val macWireMacros = "com.softwaremill.macwire" %% "macros" % V.MacWire
  val macWireUtil = "com.softwaremill.macwire" %% "util" % V.MacWire
  val macWireProxy = "com.softwaremill.macwire" %% "proxy" % V.MacWire

  object Test {
    val junit = "junit" % "junit" % V.JUnit
    val mockito = "org.mockito" % "mockito-all" % V.Mockito
  }
}


// vim: set ts=4 sw=4 et:
