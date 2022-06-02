package com.liventask.ui.order


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.liventask.R
import com.liventask.data.ItemModel
import com.liventask.data.OrderDetails
import com.liventask.databinding.ActivityAddOrderBinding
import com.liventask.ui.invoice.InvoiceActivity
import com.liventask.ui.items.ItemListDialog
import com.liventask.ui.order.adapter.OrderListAdapter
import com.liventask.utils.Constant


class AddOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOrderBinding
    private val model: AddOrderViewModel by viewModels()
    private lateinit var orderItemsAdapter: OrderListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(findViewById(R.id.myToolbar))
        init()
    }


    private fun init() {
        orderItemsAdapter = OrderListAdapter()
        binding.orderList.adapter = orderItemsAdapter
        binding.edtPayment.setOnClickListener {
            showPaymentOption(binding.paymentLayout)
        }
        binding.edtDiscount.setOnClickListener {
            showDiscountPercentage(binding.discountLayout)
        }
        binding.btnAddItem.setOnClickListener {
            showAddItemDialog()
        }

        binding.btnInvoice.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.edtCustomerName.text.toString().isEmpty()) {
            binding.edtCustomerNameLayout.error = getString(R.string.error_customer_name_empty)
            binding.edtCustomerName.requestFocus()
        } else if (binding.edtPayment.text.toString().isEmpty()) {
            binding.edtCustomerNameLayout.error = null
            binding.paymentLayout.error = getString(R.string.error_payment_mode)
        } else if (orderItemsAdapter.getAllItems().isEmpty()) {
            binding.edtCustomerNameLayout.error = null
            binding.paymentLayout.error = null
            showMessage(getString(R.string.error_add_items))
        } else if (binding.edtPaid.text.toString().isEmpty()) {
            binding.edtCustomerNameLayout.error = null
            binding.paymentLayout.error = null
        } else if (binding.edtPaid.text.toString().toDouble() <= 0) {
            binding.edtCustomerNameLayout.error = null
            binding.paymentLayout.error = null
            binding.paidLayout.error = getString(R.string.erro_valid_amount)
        } else {
            binding.edtCustomerNameLayout.error = null
            binding.paymentLayout.error = null
            binding.paidLayout.error = null
            val orderDetails = OrderDetails().apply {
                customerName = binding.edtCustomerName.text.toString()
                paymentMode = binding.edtPayment.text.toString()
                items = orderItemsAdapter.getAllItems()
                paid = binding.edtPaid.text.toString().toDouble()
                advancePayment = if (binding.edtAdvance.text.toString()
                        .isEmpty()
                ) 0.0 else binding.edtAdvance.text.toString().toDouble()
                flatDiscount = if (binding.edtFlatDiscount.text.toString()
                        .isEmpty()
                ) 0.0 else binding.edtFlatDiscount.text.toString().toDouble()
                discountPercentage = if (binding.edtDiscount.text.toString()
                        .isEmpty()
                ) 0.0 else binding.edtDiscount.text.toString().toDouble()
            }
            startActivity(Intent(this, InvoiceActivity::class.java).apply {
                putExtra(Constant.DETAILS, Gson().toJson(orderDetails))
            })
        }

    }

    private fun showPaymentOption(view: View) {
        val menu = PopupMenu(this, view)
        model.getPaymentOptions().forEach {
            menu.menu.add(it)
        }
        menu.setOnMenuItemClickListener {
            binding.edtPayment.setText(it.title.toString())
            true
        }
        menu.show()
    }

    private fun showDiscountPercentage(view: View) {
        val menu = PopupMenu(this, view)
        model.getDiscountPercentage().forEach {
            menu.menu.add(it)
        }
        menu.setOnMenuItemClickListener {
            binding.edtDiscount.setText(it.title.toString())
            true
        }
        menu.show()
    }

    private fun showAddItemDialog() {
        ItemListDialog(object : ItemListDialog.OnSubmitItems {
            override fun onSaveItems(items: ArrayList<ItemModel>) {
                if (items.isNotEmpty()) {
                    orderItemsAdapter.addItems(items)

                }
            }
        }).show(supportFragmentManager, ItemListDialog::class.java.simpleName)
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.btnInvoice, message, Snackbar.LENGTH_SHORT).show()
    }

}