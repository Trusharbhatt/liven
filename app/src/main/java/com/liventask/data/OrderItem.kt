package com.liventask.data


import com.google.gson.annotations.SerializedName

data class OrderItem(
    @SerializedName("item")
    var item: ItemModel?,
    @SerializedName("qty")
    var qty: Int
)