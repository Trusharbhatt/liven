package com.liventask.data


import com.google.gson.annotations.SerializedName

data class ItemModel(
    @SerializedName("item")
    var item: String,
    @SerializedName("price")
    var price: Double,
    var isChecked: Boolean = false

)