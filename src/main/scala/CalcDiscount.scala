import com.sun.javafx.geom.transform.Identity

import scala.io.{BufferedSource, Source}
import java.io.{File, FileOutputStream, PrintWriter}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.apache.logging.log4j.LogManager



object CalcDiscount  extends  App{


  // Initialize logger
  private val logger = LogManager.getLogger(getClass.getName)

  // Log initialization
  logger.info("Rule engine initialization started")

  //*****************************************************************//

  // The Reading part
  val source: BufferedSource = Source.fromFile("src/main/resources/TRX1000.csv")
  val lines: List[String] = source.getLines().drop(1).toList // drop header


  val f: File = new File("src/main/resources/DiscountOutput.csv")
  val writer = new PrintWriter(new FileOutputStream(f, true))


  // Define a case class to represent an order
  case class Order(orderDate: LocalDate,productName: String, expiryDate: LocalDate,  quantity: Int ,unit_price: Double ,channel: String,payment_method: String ,discount :Double)

  // The needed Variables
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // creating object from the Order class
  def toOrder(line: String): Order = {
    val orderDateStr = line.split(",")(0).split("T")(0)
    val expiryDateStr = line.split(",")(2)

    val orderDate = LocalDate.parse(orderDateStr, dateFormatter)
    val expiryDate = LocalDate.parse(expiryDateStr, dateFormatter)

    val productName = line.split(",")(1).split("-")(0)
    val quantity = line.split(",")(3).toInt

    val price = line.split(",")(4).toDouble

    val channel = line.split(",")(5)
    val payment_method = line.split(",")(6)

    Order(orderDate,  productName, expiryDate, quantity ,price ,channel ,payment_method,0)
  }

  // create list of order objects
  val ordersList = {
    lines.map(toOrder(_))
  }



  // Calculate the days between the order date and the expiry date
  def getDaysBetween(orderDate: LocalDate, expiryDate: LocalDate): Long = {
    ChronoUnit.DAYS.between(orderDate, expiryDate)
  }

  // Calculate the discount based on the expiration days
  def calcExpireDiscount(order: Order): Double = {
    val daysBetween = ChronoUnit.DAYS.between(order.orderDate, order.expiryDate)
    if (daysBetween < 30 && daysBetween > 0) {
      (30 - daysBetween )
    } else {
      0
    }
  }

  // Calculate the discount based on specific categories
  def categoryDiscount(productName: String): Double = {
    val category = productName.split("-")(0).trim.toLowerCase

    category match {
      case "cheese" => 10
      case "wine" => 5
      case _ => 0
    }
  }

  // Calculate the discount for orders placed on the 23rd of March
  def the23rd_of_March(orderDate: LocalDate): Double = {
    if (orderDate.getDayOfMonth == 23 && orderDate.getMonthValue == 3) {
      50
    } else {
      0
    }
  }

  // Calculate the discount based on the quantity sold from the product
  def quantitybasedDiscount(quantity: Int): Double = {
    if(quantity > 6 && quantity < 9) {
      5
    }
    else if(quantity > 10 && quantity < 14) {
      7
    }
    else if(quantity > 15) {
      10
    }
    else {
      0
    }
  }

  // Apply qualifiers and calculate discount
  def calculateDiscount(order: Order): Double = {
    val discounts = List(calcExpireDiscount(order),
      categoryDiscount(order.productName),
      the23rd_of_March(order.orderDate),
      quantitybasedDiscount(order.quantity)
    )

    val qualifiedDiscounts = discounts.filter(_ > 0)
    if (qualifiedDiscounts.isEmpty) {
      0
    } else if (qualifiedDiscounts.size > 1) {
      qualifiedDiscounts.sortBy(x => x).reverse.take(2).sum / 2
    } else {
      qualifiedDiscounts.head
    }
  }

  // Calculate discounts for all orders
  val discountedOrders: List[Order] = ordersList.map { order =>
    val discount = calculateDiscount(order)
    val newPrice = order.unit_price-discount
    order.copy(unit_price = newPrice,discount = discount) // copy the object to have all values except the one I Changed(Discount)
  }

  // Write discounted orders to the output file
  discountedOrders.foreach { order =>
    val line = s"${order.orderDate},${order.productName},${order.expiryDate},${order.quantity},${order.unit_price},${order.channel},${order.payment_method},${order.discount}"
    writer.println(line)
  }

  writer.close()

  ///  ***********************
  // Log completion
  logger.info("Discount calculation completed successfully")


}








