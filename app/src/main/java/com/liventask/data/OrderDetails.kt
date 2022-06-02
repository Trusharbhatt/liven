package com.liventask.data


import com.google.gson.annotations.SerializedName

data class OrderDetails(
    @SerializedName("advancePayment")
    var advancePayment: Double = 0.0,
    @SerializedName("items")
    var items: ArrayList<OrderItem> = arrayListOf(),
    @SerializedName("customerName")
    var customerName: String = "",
    @SerializedName("discountPercentage")
    var discountPercentage: Double = 0.0,
    @SerializedName("flatDiscount")
    var flatDiscount: Double = 0.0,
    @SerializedName("paymentMode")
    var paymentMode: String = "",
    @SerializedName("paid")
    var paid: Double = 0.0,
)