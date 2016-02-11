package tabula

import scala.util.control.Exception._

trait Namer {
  val simpleName: String = {
    val name = allCatch
      .opt(getClass.getSimpleName)
      .getOrElse(getClass.getName.split("\\.").last)
    if (name.endsWith("$")) name.split("\\W").last
    else name
  }
}

class Foo extends Namer
object Bar {
  class Baz extends Namer
  object Quux extends Baz
}

object Test {
  def main(argv: Array[String]) {
    new Foo
    new Bar.Baz
    object haha extends Bar.Baz
    Bar.Quux
  }
}
