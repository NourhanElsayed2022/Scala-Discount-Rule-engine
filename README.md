# Scala Discounts Engine

This Scala project calculates discounts for orders based on various criteria such as expiration date, product category, purchase channel, payment method, and quantity purchased.

## **Project Details:**

This project implements a discount calculation engine in Scala. It reads order data from a CSV file, applies various discount rules, and writes the discounted orders to an output CSV file. The discount rules include expiration date discounts, category-based discounts, special discounts for orders placed on specific dates, quantity-based discounts, app purchase discounts, and payment method discounts.

## **Discount Calculation:**

Discounts are calculated based on the following criteria:

- **Expiration Date Discount:** If the number of days remaining between the purchasing date and the expiration date is less than 30 days, a discount of (30 - (days between)) is applied. Otherwise, no discount is applied.

For example:

- If 29 days remain ‐> 1% discount.
- If 28 days remain ‐> 2% discount.
- If 27 days remain ‐> 3% discount. etc …
- **Product Category Discount:** If the product category is "Cheese", a discount of 10% is applied. And If the product category is "Wine", a discount of 5% is applied. Otherwise, no discount is applied.
- **Specific Day of Month Discount:** If the purchasing date is March 23, a special discount of 50% is applied. Otherwise, no discount is applied.
- **Quantity Discount:** If the quantity of products purchased is more than 5 of the same product, a discount will be applied.
    - If the quantity is between 6 and 9, a discount of 5% is applied.
    - If the quantity of products purchased is between 10 and 14, a discount of 7% is applied.
    - If the quantity of products purchased is 15 or more, a discount of 10% is applied.
    - Otherwise, no discount is applied.
- **Purchasing Channel Discount:** Sales that are made through the "App" will have a special discount. A discount of the quantity rounded up to the nearest multiple of 5 is applied. Otherwise, no discount is applied.

For example:

- If quantity: 1, 2, 3, 4, 5 ‐> discount 5%.
- If quantity 6, 7, 8, 9, 10 ‐> discount 10%.
- If quantity 11, 12, 13, 14, 15 ‐> discount 15%. etc …
- **Payment Method Discount:** If the payment method is "Visa", a discount of 5% is applied. Otherwise, no discount is applied

## **Main Rules:**

- Transactions that didn't qualify for any discount will have a 0% discount.
- Transactions that qualified for more than one discount will get the top 2 and get their average.
- After ingesting the data and calculating the discount the final price will be calculated and loaded into the output files.

## **Project Main Functions:**

- **calcExpireDiscount:** Calculates the discount to be applied to a product based on its expiration date.
- **categoryDiscount:** Determines the discount to be applied to a product based on its category.
- **the23rd_of_March:** Determines the discount to be applied to a purchase made on 23 march.
- **quantitybasedDiscoun**t : Determines the discount to be applied to a product based on its purchased quantity.
- **appDiscount:** Determines the discount to be applied to a purchase based on the purchasing channel.
- **paymentDiscount:** Determines the discount to be applied to a purchase based on the payment method.
- **calculateDiscount:** Determines the final price after applying the discount.

## **How does It work?**

This Scala project calculates discounts for orders based on various criteria such as expiration date proximity, product category, special dates, quantity, app purchases, and payment method. It reads order data from a CSV file (**`TRX1000.csv`**) , applies the discount rules to each order, and writes the discounted orders to another CSV file. (**`DiscountOutput.csv`**).

 The process is logged, indicating the initialization and completion of the discount calculation.

In the format :       `TIMESTAMP LOGLEVEL MESSAGE`

## **Project Main Files:**

- **`TRX1000.csv`** Contains sample data to be used on the Rule Engine.
- **`DiscountOutput.csv`** Contains the output result with discount percentage and the total price after discount.
- `rules_engine.log` contains the logs of the Rule Engine project.

## **Tools and Technologies:**

- Scala Programming Language.
- IntelliJ IDE.

## **Dependencies:**

- Scala 2.13.13
- Dependencies

```scala
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.14.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.14.1"
```

## **How To Use**

1. Install dependencies ( Scala, `log4j-1.2-api-2.23.1.jar` , `log4j-core-2.23.1.jar` ).
2. Configure the `log4j2.xml` file in the `src/main/Resources` directory if needed.
3. Prepare your data file (`TRX1000.csv`) with the required columns.
4. Run the project using IDE like (IntelliJ) or Command Line.
5. View the logs in the `logs` directory for processing details.

## The Raw Data:

![in.JPG](Scala%20Discounts%20Engine%2074f17010014647368ca78bd6c1618717/in.jpg)

## The Output:

![out.JPG](Scala%20Discounts%20Engine%2074f17010014647368ca78bd6c1618717/out.jpg)