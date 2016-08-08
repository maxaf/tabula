package tabula.test

import tabula._, Tabula._, Column._
import shapeless._
import cats.implicits._

import org.specs2.mutable._
import org.joda.time._
import org.apache.commons.lang3.text.WordUtils.capitalize
import org.apache.commons.lang3.RandomStringUtils.randomAscii

// a pretend data model

case class UselessItem(name: String, price: Double, tags: Map[String, String] = Map.empty, utterlyUseless: Boolean)
case class PretentiousPurveyor(name: String, location: String)
case class Purchase(item: UselessItem, date: Option[DateTime], from: PretentiousPurveyor, quantity: Int)

// some test data

object Items {
  val justSomeCoatRack = UselessItem("honest abe", 90.39, tags = Map("foo" -> randomAscii(10)), utterlyUseless = false)
  val cheeseParkingSpot = UselessItem("fancy cheese board", 39.95, tags = Map("foo" -> randomAscii(10), "bar" -> randomAscii(10)), utterlyUseless = true)
  val whatIsThis = UselessItem("faux professional tool pouch", 48.00, tags = Map("foo" -> randomAscii(10), "quux" -> randomAscii(10)), utterlyUseless = false)
}

object PlacesNormalPeopleDoNotGo {
  val SchizophrenicMonkey = PretentiousPurveyor("Tinkering Monkey", "SF Bay Area")
  val BrooklynSlateWtf = PretentiousPurveyor("Brooklyn Slate Co.", "Brokelyn")
  val HeritageInsanityCo = PretentiousPurveyor("Heritage Leather Co.", "Somewhere in Cali")
}

object Purchases {
  import Items._
  import PlacesNormalPeopleDoNotGo._

  val * = {
    Purchase(item = justSomeCoatRack, date = Some(DateTime.now), from = SchizophrenicMonkey, quantity = 1) ::
      Purchase(item = cheeseParkingSpot, date = None, from = BrooklynSlateWtf, quantity = 3) ::
      Purchase(item = whatIsThis, date = Some(DateTime.now), from = HeritageInsanityCo, quantity = 5) ::
      Nil
  }
}

// column descriptions

// what we bought
object ItemName extends Column((_: Purchase).item.name)

// how much we paid
object Total extends Column((p: Purchase) => p.item.price * p.quantity)

// how many did we buy?
object Quantity extends Column((_: Purchase).quantity)

// where we bought it
object PurchaseLocation extends Column((_: Purchase).from.location)

// date of purchase
object DateOfPurchase extends Column((_: Purchase).date)

// was it utterly & completely useles?
object Useless extends Column((_: Purchase).item.utterlyUseless)

// tags
case class Tag(name: String) extends Column((_: Purchase).item.tags.get(name))
object Tags extends ListColumn(
  Tag("foo") @@ "tag foo" ::
    Tag("bar") @@ "tag bar" ::
    Tag("baz") @@ "tag baz" ::
    Tag("quux") @@ "tag quux" :: Nil
)

// transformer column: capitalize words
object Capitalize extends Column(capitalize)

// unlimited extensibility via type classes!
object Extensibility {
  import scala.xml._

  // we'll output cells of type Cell[NodeSeq] via a function that
  // procudes objects of type HTML
  case class HTML(nodes: NodeSeq)

  // provide a way of lazily converting HTML => Cell[NodeSeq]
  implicit object HTMLNodeSeqCellulizer extends Cellulizer[HTML, NodeSeq](_.nodes)

  // here's a column that produces HTML from Purchase-s
  object Title extends Column((p: Purchase) => HTML(<title>{ p.item.name }</title>)) with Namer

  // create custom console output that overrides some default formats and
  // implements conversion of NodeSeq-s to text
  object MyConsole extends Console(minWidth = 15) {
    implicit object NodeSeqFormatter extends SimpleFormatter[NodeSeq] {
      def apply(value: Option[NodeSeq]) = value.map(_.toString) :: Nil
    }
  }
}

import Extensibility._

object ShowcaseSpec {
  object columns extends Columns(
    (ItemName | Capitalize) @@ "Item Name" ::
      Title ::
      Total @@ "Purchase Total ($)" ::
      Quantity @@ "Number of items bought" ::
      PurchaseLocation @@ "Bought At" ::
      DateOfPurchase @@ "Date of Purchase" ::
      Useless @@ "Was it useless?" ::
      Tags ::
      HNil
  )
}

// let's do it!
class ShowcaseSpec extends Specification {
  "a purchase history" should {
    import ShowcaseSpec._
    "print out a list of things we've bought" in {
      columns.write(MyConsole)(_.toConsole())(Purchases.*.iterator)
      success
    }
  }
}
