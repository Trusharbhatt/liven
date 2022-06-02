package com.liventask.ui.order

import androidx.lifecycle.ViewModel
import com.liventask.enum.PaymentMode

class AddOrderViewModel : ViewModel() {

    fun getPaymentOptions(): ArrayList<String> {
        val paymentOptions = arrayListOf<String>()
        paymentOptions.apply {
            add(PaymentMode.CASH.value)
            add(PaymentMode.CARD.value)
        }
        return paymentOptions
    }


    fun getDiscountPercentage(): ArrayList<String> {
        val paymentOptions = arrayListOf<String>()
        paymentOptions.apply {
            add("0")
            add("5")
            add("10")
            add("15")
            add("20")
        }
        return paymentOptions
    }

}