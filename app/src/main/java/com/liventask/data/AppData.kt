package com.liventask.data

object AppData {
    val itemList = lazy {
        arrayListOf(
            ItemModel("Big Brekkies", 16.0),
            ItemModel("Bruschetta", 8.0),
            ItemModel("Poached Eggs", 12.0),
            ItemModel("Coffee", 5.0),
            ItemModel("Tea", 3.0),
            ItemModel("Soda", 4.0),
            ItemModel("Garden Salad", 10.0),
        )
    }
}