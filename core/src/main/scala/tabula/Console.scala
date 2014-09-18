package tabula

import Tabula._
import java.io.{ OutputStream, PrintWriter }
import org.joda.time._

abstract class Console(minWidth: Int = 15) extends Format {
  type Base = Option[String]
  type Row = List[Option[String]]

  object RowProto extends RowProto {
    def emptyRow = Nil
    def appendCell[C](cell: Cell[C])(row: Row)(implicit fter: Formatter[C]) =
      fter(cell).foldLeft(row)((acc, elem) => appendBase(elem)(acc))
    def appendBase[T <: Base](cell: T)(row: Row) = row ::: cell :: Nil
  }

  class Spawn(names: List[Option[String]]) extends WriterSpawn(names) {
    def padWidth[A](width: Int)(op: Int => A): A = op(if (width < minWidth) minWidth else width)
    val widths = names.map(_.map(_.size).getOrElse(minWidth))
    def padTo(width: Int, s: String) = ("%1$-"+width+"."+width+"s").format(s)
    def padRow(ss: List[Option[String]]) = {
      widths
        .zip(ss)
        .map { case (width, s) => padWidth(width)(w => padTo(w, s.getOrElse(""))) }
        .mkString("| ", " | ", " |")
    }
    val header = padRow(names)
    val hr = "+"+("-" * (header.size - 2))+"+"

    def toPrintWriter(printer: PrintWriter) = new Writer {
      override def start() {
        printer.println(hr)
        printer.println(header)
        printer.println(hr)
      }
      def writeMore(rows: Iterator[Row]) {
        for (row <- rows)
          printer.println(padRow(row))
      }
      override def finish() {
        printer.println(hr)
      }
    }

    def toStream(out: OutputStream) = toPrintWriter(new PrintWriter(out, true))
  }

  def writer(names: List[Option[String]]) = new Spawn(names)

  implicit object StringFormatter extends SimpleFormatter[String] {
    def apply(value: Option[String]) = value :: Nil
  }

  implicit object DoubleFormatter extends SimpleFormatter[Double] {
    def apply(value: Option[Double]) = value.map(_.toString) :: Nil
  }

  implicit object DateTimeFormatter extends SimpleFormatter[DateTime] {
    def apply(value: Option[DateTime]) = value.map(dt => s"$dt") :: Nil
  }
}
