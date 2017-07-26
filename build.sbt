import Deps._

enablePlugins(ScalafmtPlugin)

lazy val concurrencySettings = Seq(
  parallelExecution := true,
  concurrentRestrictions in Global := {
    Tags.limitAll(
      if (parallelExecution.value)
        java.lang.Runtime.getRuntime.availableProcessors
      else 1) :: Nil
  }
)

lazy val crossSettings = Seq(
  scalaVersion := crossScalaVersions.value.head,
  crossScalaVersions := Seq("2.12.2", "2.11.11", "2.10.6")
)

lazy val baseSettings = Seq(
  organization := "com.bumnetworks",
  packageOptions in (Compile, packageBin) +=
    Package.ManifestAttributes(
      "PackageDate" -> new java.util.Date().toString,
      "PackageHost" -> java.net.InetAddress.getLocalHost.getHostName
    ),
  updateOptions := updateOptions.value.withCachedResolution(true),
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:reflectiveCalls",
    "-language:postfixOps",
    "-Yrangepos"
  ),
  javacOptions ++= Seq(
    "-source",
    "1.8",
    "-target",
    "1.8",
    "-Xlint"
  ),
  homepage := Some(url("https://github.com/maxaf/tabula")),
  version := "0.1.4",
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true,
  publishTo <<= (version) { v =>
    val repo = file(".") / ".." / "repo"
    Some(
      Resolver.file("repo",
                    if (v.trim.endsWith("SNAPSHOT")) repo / "snapshots"
                    else repo / "releases"))
  }
) ++ concurrencySettings ++ crossSettings

lazy val `tabula-core` = module("core")
  .settings(
    libraryDependencies ++= CoreDeps
  )

lazy val `tabula-json` = module("json")
  .settings(
    libraryDependencies ++= JsonDeps
  ) dependsOn (`tabula-core` % "compile->compile;test->test")

lazy val `tabula-excel` = module("excel")
  .settings(
    libraryDependencies ++= ExcelDeps
  ) dependsOn (`tabula-core` % "compile->compile;test->test")

lazy val tabula = (project in file("."))
  .settings(concurrencySettings, crossSettings, publish := {})
  .aggregate(`tabula-core`, `tabula-json`, `tabula-excel`)

def module(name: String) =
  Project(s"tabula-${name.replaceAll("/", "-")}", file(name))
    .settings(baseSettings)
