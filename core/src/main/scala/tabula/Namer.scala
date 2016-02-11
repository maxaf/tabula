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
