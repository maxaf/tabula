package tabula.excel.test

import java.io.{ File, FileOutputStream }
import tabula._
import Tabula._
import tabula.excel._
import tabula.test._
import org.specs2.mutable._
import scala.xml._
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook

abstract class MyExcelSheet(name: String)(implicit workbook: Workbook) extends ExcelSheet(name) {
  implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
    def apply(value: Option[NodeSeq]) = implicitly[Formatter[String]].apply(value.map(_.toString))
  }
}

class ExcelSpec extends Specification {
  import ShowcaseSpec._
  "a Excel output" should {
    "produce Excel output" in {
      Excel(() => new HSSFWorkbook()) {
        implicit wb =>
          object sheet1 extends MyExcelSheet("excel spec - sheet one")
          object sheet2 extends MyExcelSheet("excel spec - sheet two")
          object sheet3 extends MyExcelSheet("excel spec - sheet three")

          val stream = new java.io.ByteArrayOutputStream
          val streamAlternate = new java.io.ByteArrayOutputStream

          val file = File.createTempFile(getClass.getName+".", ".xls")
          val fileWithEverything = File.createTempFile(getClass.getName+".", ".xls")

          val writer1 = sheet1.writer(columns).toStream(stream)
          val writer2 = sheet2.writer(columns).toStream(stream)
          val writer3 = sheet3.writer(columns).toStream(streamAlternate)

          writer1.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet1))
          writer2.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet2))
          writer3.write(for (purchase <- Purchases.*.iterator) yield cellsF(purchase).row(sheet3))

          new FileOutputStream(file).write(stream.toByteArray)
          new FileOutputStream(fileWithEverything).write(streamAlternate.toByteArray)

          println("only contains one sheet")
          println(file)

          println("contains everything")
          println(fileWithEverything)
      }
      success
    }
  }
}
