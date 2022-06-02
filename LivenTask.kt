import java.util.Scanner

enum class PAYMENT_MODE(val value: String) {
    CASH("cash"),
    CARD("card")
}

private val bb = "Big Brekkies "
private val bc = "Bruschetta   "
private val pe = "Poached      "
private val cf = "Coffee       "
private val tea = "Tea          "
private val soda = "Soda         "
private val gs = "Garden Salad "

private val subTotalText = "Sub Total        "
private val taxText = "TAX (Surcharges) "
private val discountText = "Discount         "
private val advance = "Advance Payment  "
private val total = "Total            "
private val paidText = "Paid Amount      "
private val returnedText = "Returned Amount  "
private val remainText = "Remaining Amount "

data class ItemData(val name: String?, val price: Double = 0.0, val qty: Int = 0)

data class OrderDetais(
    val name: String?,
    val items: List<ItemData>?,
    val advance: Double = 0.0,
    val mode: PAYMENT_MODE?,
    val discount: Int = 0,
    val paid: Double = 0.0
)


fun main(args: Array<String>) {
    readData()
}

private fun readData() {
    // Here we can read data by user input for each entry but I took static data as per task
    // For Dynamic entry I developed simple android app, details provided in mail.

    val items1 = arrayListOf<ItemData>()
    val items2 = arrayListOf<ItemData>()
    val items3 = arrayListOf<ItemData>()
    items1.apply {
        add(ItemData(bb, 16.0, 3))
        add(ItemData(bc, 8.0, 3))
        add(ItemData(pe, 12.0, 3))
        add(ItemData(cf, 5.0, 2))
        add(ItemData(tea, 3.0, 1))
        add(ItemData(soda, 4.0, 3))
    }

    items2.apply {
        add(ItemData(tea, 3.0, 1))
        add(ItemData(cf, 3.0, 3))
        add(ItemData(soda, 4.0, 1))
        add(ItemData(bb, 16.0, 3))
        add(ItemData(pe, 12.0, 1))
        add(ItemData(gs, 10.0, 1))
    }

    items3.apply {
        add(ItemData(tea, 3.0, 2))
        add(ItemData(cf, 3.0, 3))
        add(ItemData(soda, 4.0, 2))
        add(ItemData(bc, 8.0, 5))
        add(ItemData(bb, 16.0, 5))
        add(ItemData(pe, 12.0, 2))
        add(ItemData(gs, 10.0, 3))
    }

    val orders = arrayListOf<OrderDetais>()
    orders.apply {
        add(OrderDetais("Group 1", items1, 0.0, PAYMENT_MODE.CASH, 0, 140.0))
        add(OrderDetais("Group 2", items2, 0.0, PAYMENT_MODE.CARD, 0, 80.0))
        add(OrderDetais("Group 3", items3, 50.0, PAYMENT_MODE.CASH, 25, 120.0))
    }

    orders.forEach { it ->
        printInvoice(it)
    }

}


private fun printInvoice(orderDetails: OrderDetais) {
    println("\n\n\n\n")
    println("*********************  INVOICE **************************")
    println("Name : ${orderDetails.name}                       PaymentMode : ${orderDetails.mode!!.value}")
    println("=========================================================")
    println("Item                   Unit Price    Qty.         Amount ")
    println("---------------------------------------------------------")
    orderDetails.items!!.forEach { it ->
        println("${it.name}\t\t\t  $ ${it.price}\t\t  ${it.qty}\t\t\t  $ ${(it.qty * it.price)}")
    }
    println("---------------------------------------------------------")
    val subTotal = getSubTotal(orderDetails.items)
    println(String.format("$subTotalText \t\t\t\t\t\t\t\t  $ %.2f", subTotal))
    var tax = 0.0
    var discount = 0.0
    if (orderDetails.mode.value == PAYMENT_MODE.CARD.value) {
        discount = subTotal * 0.10
        tax = subTotal * 0.012
        println(String.format("$discountText\t\t\t\t\t\t\t\t- $ %.2f", discount))
        println(String.format("$taxText \t\t\t\t\t\t\t    + $ %.2f", tax))
    }

    if (orderDetails.discount > 0) {
        discount = orderDetails.discount.toDouble()
        println(
            String.format(
                "$discountText\t\t\t\t\t\t\t\t-  %.2f",
                orderDetails.discount.toDouble()
            )
        )
    }

    if (orderDetails.advance > 0) {
        println(String.format("$advance\t\t\t\t\t\t\t\t-  $ %.2f", orderDetails.advance))
    }
    println("---------------------------------------------------------")

    val finalTotal = (subTotal + tax) - (discount.toDouble() - orderDetails.advance)
    println(String.format("$total \t\t\t\t\t\t\t\t $ %.2f", finalTotal))
    println("---------------------------------------------------------")


    println(String.format("$paidText  \t\t\t\t\t\t\t\t $ %.2f", orderDetails.paid.toDouble()))

    var returned = orderDetails.paid - finalTotal
    if (returned < 0) returned = 0.0
    println(String.format("$returnedText  \t\t\t\t\t\t\t\t $ %.2f", returned))

    var remaining = finalTotal - orderDetails.paid
    if (remaining < 0) remaining = 0.0
    println(String.format("$remainText  \t\t\t\t\t\t\t\t $ %.2f", remaining))

    println("\n\n\n\n")


}

private fun getSubTotal(list: List<ItemData>?): Double {
    var total = 0.0

    list!!.forEach { item ->
        total += (item.qty * item.price)
    }

    return total
}

