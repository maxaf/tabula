package tabula

import shapeless._
import shapeless.ops.hlist._
import java.io.{ File, OutputStream, FileOutputStream }

trait Writers {
  self: Format =>

  abstract class Writer {
    def start() {}
    def writeMore(rows: Iterator[Row]): Unit
    def write(rows: Iterator[Row]) {
      start()
      writeMore(rows)
      finish()
    }
    def finish() {}
  }

  abstract class WriterFactory(protected val names: List[Option[String]]) {
    def toStream(out: OutputStream): Writer
    def toFile(file: File) = toStream(new FileOutputStream(file))
    def toConsole() = toStream(System.out)
  }

  type Factory <: WriterFactory

  def writer[F, T, C, NcT <: HList, Col](cols: Col :: NcT)(implicit ev: Col <:< Column[F, T, C], tl: ToList[Col :: NcT, Column[_, _, _]]): Factory = writer(NamedColumn.names(cols))
  def writer(names: List[Option[String]]): Factory
}
