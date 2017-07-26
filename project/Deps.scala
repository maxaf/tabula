import sbt._, Keys._

object Versions {
  val JodaTimeVersion = "2.9.9"
  val JodaConvertVersion = "1.8.2"
  val ShapelessVersion = "2.3.2"
  val PoiVersion = "3.14"
  val Json4sVersion = "3.5.2"
  val CommonsLangVersion = "3.6"
  val SpecsVersion = "3.9.1"
  val CatsVersion = "0.9.0"
}

object Deps {
  import Versions._

  val cats = "org.typelevel" %% "cats" % CatsVersion
  val joda_time = "joda-time" % "joda-time" % JodaTimeVersion
  val joda_convert = "org.joda" % "joda-convert" % JodaConvertVersion
  val commons_lang = "org.apache.commons" % "commons-lang3" % CommonsLangVersion % "test"
  val poi = "org.apache.poi" % "poi" % PoiVersion
  val json4s = "org.json4s" %% "json4s-native" % Json4sVersion
  val shapeless = "com.chuusai" %% "shapeless" % ShapelessVersion
  val Reflection = Seq(libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value)
  val Specs2 = "org.specs2" %% "specs2-core" % "3.9.1" % "test"

  val CoreDeps = Seq(cats, joda_time, joda_convert, commons_lang, shapeless, Specs2)
  val JsonDeps = Seq(json4s)
  val ExcelDeps = Seq(poi)
}
