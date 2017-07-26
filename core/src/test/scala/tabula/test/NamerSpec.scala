package tabula.test

import tabula._
import Tabula._
import Column._
import shapeless._
import shapeless.HList._
import org.specs2.mutable._

object NamerSpec {
  case class Person(first_name: String, last_name: String)
  object FirstName extends Column((_: Person).first_name) with Namer
  object first_name extends Column((_: Person).first_name) with Namer
  object One {
    object FirstName extends Column((_: Person).first_name) with Namer
    object first_name extends Column((_: Person).first_name) with Namer
  }
  class Two {
    object FirstName extends Column((_: Person).first_name) with Namer
    object first_name extends Column((_: Person).first_name) with Namer
  }
  trait Three {
    object FirstName extends Column((_: Person).first_name) with Namer
    object first_name extends Column((_: Person).first_name) with Namer
  }
}

class NamerSpec extends Specification {
  import NamerSpec._
  "a namer" should {
    "detect names correctly" in {
      FirstName.simpleName must_== "FirstName"
      first_name.simpleName must_== "first_name"

      One.FirstName.simpleName must_== "FirstName"
      One.first_name.simpleName must_== "first_name"

      val two = new Two
      two.FirstName.simpleName must_== "FirstName"
      two.first_name.simpleName must_== "first_name"

      object Two extends Two
      Two.FirstName.simpleName must_== "FirstName"
      Two.first_name.simpleName must_== "first_name"

      val three = new Three {}
      three.FirstName.simpleName must_== "FirstName"
      three.first_name.simpleName must_== "first_name"

      object Three extends Three
      Three.FirstName.simpleName must_== "FirstName"
      Three.first_name.simpleName must_== "first_name"

      success
    }
  }
}
