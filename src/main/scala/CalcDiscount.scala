// Importing required libraries
import com.sun.javafx.geom.transform.Identity
import scala.io.{BufferedSource, Source}
import java.io.{File, FileOutputStream, PrintWriter}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.apache.logging.log4j.LogManager


object CalcDiscount extends App {

  // Initialize logger
  private val logger = LogManager.getLogger(getClass.getName)

  // Log initialization
  logger.info("Rule engine initialization started")

  //*****************************************************************//

  // The Reading part
  val source: BufferedSource = Source.fromFile("src/main/resources/TRX1000.csv")
  val lines: List[String] = source.getLines().drop(1).toList // drop header

  // Output file setup
  val f: File = new File("src/main/resources/DiscountOutput.csv")
  val writer = new PrintWriter(new FileOutputStream(f, true))

  // Define a case class to represent the orders
  case class Order(orderDate: LocalDate, productName1: String,productName2: Array[String], expiryDate: LocalDate, quantity: Int, unit_price: Double, channel: String, payment_method: String, discount: Double)

  // Define the Date formatter
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // Function to convert each line to an Order object
  def toOrder(line: String): Order = {
    val orderDateStr = line.split(",")(0).split("T")(0)
    val expiryDateStr = line.split(",")(2)
    val orderDate = LocalDate.parse(orderDateStr, dateFormatter)
    val expiryDate = LocalDate.parse(expiryDateStr, dateFormatter)
    val productName1 = line.split(",")(1).split("-")(0)
    val productName2 = line.split(",")(1).split("-").tail
    val quantity = line.split(",")(3).toInt
    val price = line.split(",")(4).toDouble
    val channel = line.split(",")(5)
    val payment_method = line.split(",")(6)
    Order(orderDate, productName1,productName2, expiryDate, quantity, price, channel, payment_method, 0)
  }

  // Create list of order objects
  val ordersList = lines.map(toOrder(_))

  // Function to Calculate the days between the order date and the expiry date
  def getDaysBetween(orderDate: LocalDate, expiryDate: LocalDate): Long = {
    ChronoUnit.DAYS.between(orderDate, expiryDate)
  }

  // function to Calculate the discount based on the expiration days
  def calcExpireDiscount(order: Order): Double = {
    val daysBetween = ChronoUnit.DAYS.between(order.orderDate, order.expiryDate)
    if (daysBetween < 30 && daysBetween > 0) {
      (30 - daysBetween)
    } else {
      0
    }
  }

  // Function to Calculate the discount based on specific categories
  def categoryDiscount(productName: String): Double = {
    val category = productName.split("-")(0).trim.toLowerCase
    category match {
      case "cheese" => 10
      case "wine" => 5
      case _ => 0
    }
  }

  // Function to Calculate the discount for orders placed on the 23rd of March
  def the23rd_of_March(orderDate: LocalDate): Double = {
    if (orderDate.getDayOfMonth == 23 && orderDate.getMonthValue == 3) {
      50
    } else {
      0
    }
  }

  // Function to Calculate the discount based on the quantity sold from the product
  def quantitybasedDiscount(quantity: Int): Double = {
    if (quantity > 6 && quantity <= 9) {
      5
    } else if (quantity > 10 && quantity <= 14) {
      7
    } else if (quantity >= 15) {
      10
    } else {
      0
    }
  }

  // Function to Calculate The discount for the Products that sold through the Application
  def appDiscount(order: Order): Double = {
    if (order.channel.toLowerCase == "app") {
      // Round up the quantity to the nearest multiple of 5
      val roundedQuantity = Math.ceil(order.quantity.toDouble / 5) * 5
      if (roundedQuantity == 5) {
        5
      } else if (roundedQuantity == 10) {
        10
      } else if (roundedQuantity == 15) {
        15
      } else {
        roundedQuantity
      }
    } else {
      0
    }
  }

  // Function to Calculate The discount for the Products that Purchased using Visa
  def paymentDiscount(paymentMethod: String): Double = {
    if (paymentMethod.toLowerCase == "visa") {
      5
    } else {
      0
    }
  }

  /*
  This Function Calculate the final discount for each order based on 3 steps:
          1- Apply all the discount functions and set their results in a list
          2- Filter te list to keep the their results greater than 1 in the new list "qualifiedDiscounts"
           Note: if the function result greater 1 , this mean that the order is Qualified fot this Discount
          3- If the qualifiedDiscounts list size =0 , this mean that the order will not have any discount
           --> If the size =1 , there is only one  discount and will be applied on the order.
           --> If the size more than 1 , the greats two average will be applied on the order.
   */
  def calculateDiscount(order: Order): Double = {
    val discounts = List(
      calcExpireDiscount(order),
      categoryDiscount(order.productName1),
      the23rd_of_March(order.orderDate),
      quantitybasedDiscount(order.quantity),
      appDiscount(order),
      paymentDiscount(order.payment_method)
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
    order.copy(discount = discount) // Copy the object to have all values except the one I Changed(Discount)
  }

  // Write headers to the output file
  val header = "Order Date,Product Name,Expiry Date,Quantity,Unit Price,Channel,Payment Method,Order Before Discount,Discount,Order After Discount"
  writer.println(header)

  // Write discounted orders to the output file
  discountedOrders.foreach { order =>
    // Format the line for writing to the output file
    val orderBeforeDiscount = BigDecimal(order.quantity * order.unit_price).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    val orderAfterDiscount = BigDecimal(orderBeforeDiscount - (orderBeforeDiscount * order.discount / 100)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    val line = s"${order.orderDate},${order.productName1}-${order.productName2.mkString(" ")},${order.expiryDate},${order.quantity},${order.unit_price},${order.channel},${order.payment_method},${orderBeforeDiscount},${order.discount}%,${orderAfterDiscount}"
    writer.println(line)
  }

  // Close the output file writer
  writer.close()

//********************************************************************************************//
  // Log completion
  logger.info("Discount calculation completed successfully")
}
