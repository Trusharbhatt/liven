package com.liventask.ui.invoice


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.liventask.R
import com.liventask.data.OrderDetails
import com.liventask.data.OrderItem
import com.liventask.enum.Headers
import com.liventask.enum.PaymentMode
import com.liventask.utils.Constant
import com.trushar.pdfcreator.activity.PDFCreatorActivity
import com.trushar.pdfcreator.utils.PDFUtil.PDFUtilListener
import com.trushar.pdfcreator.views.PDFBody
import com.trushar.pdfcreator.views.PDFFooterView
import com.trushar.pdfcreator.views.PDFHeaderView
import com.trushar.pdfcreator.views.basic.*
import java.io.File
import java.net.URLConnection
import java.util.*


class InvoiceActivity : PDFCreatorActivity() {


    private lateinit var orderDetails: OrderDetails
    private val halfWeight = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.WRAP_CONTENT, 0.50f
    )

    private val headers =
        arrayListOf("Index", "Item", "Unit Price", "Quantity", "Amount")

    private var grandTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        orderDetails =
            Gson().fromJson(intent.getStringExtra(Constant.DETAILS), OrderDetails::class.java)

        createPDF(getString(R.string.invoice), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                Toast.makeText(
                    this@InvoiceActivity,
                    getString(R.string.str_invoice_created),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(
                    this@InvoiceActivity,
                    getString(R.string.str_error_invoice),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun getHeaderView(forPage: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)

        val horizontalView = PDFHorizontalView(applicationContext)

        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString(getString(R.string.title_invoice))
        word.setSpan(
            ForegroundColorSpan(Color.DKGRAY),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        pdfTextView.text = word
        pdfTextView.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            ).apply {
                setMargins(0, 10, 0, 10)
            }
        )
        pdfTextView.view.gravity = Gravity.CENTER
        pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)

        horizontalView.addView(pdfTextView)

        headerView.addView(horizontalView)

        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        headerView.addView(lineSeparatorView1)





        return headerView
    }

    override fun getBodyViews(): PDFBody {
        val pdfBody = PDFBody()
        val lineSeparatorView1 = PDFLineSeparatorView(
            applicationContext
        ).setBackgroundColor(Color.BLACK)
        pdfBody.addView(lineSeparatorView1)

        pdfBody.addView(getCustomerRow())

        pdfBody.addView(
            PDFLineSeparatorView(
                applicationContext
            ).setBackgroundColor(Color.BLACK)
        )

        pdfBody.addView(getHeaders())

        pdfBody.addView(getOrderDetails())

        pdfBody.addView(
            PDFLineSeparatorView(
                applicationContext
            ).setBackgroundColor(Color.BLACK)
        )

        pdfBody.addView(getSubTotalDetails())

        pdfBody.addView(
            PDFLineSeparatorView(
                applicationContext
            ).setBackgroundColor(Color.BLACK)
        )
        pdfBody.addView(
            getSubTotalRaw(
                getString(R.string.str_total),
                String.format("$ %.2f", grandTotal)
            ).apply {
                paddingTop = 10
                paddingBottom = 10
            }
        )

        pdfBody.addView(
            PDFLineSeparatorView(
                applicationContext
            ).setBackgroundColor(Color.BLACK)
        )

        pdfBody.addView(
            getSubTotalRaw(
                getString(R.string.str_paid),
                String.format("$ %.2f", orderDetails.paid)
            ).apply {
                paddingTop = 20
                paddingBottom = 20
            }
        )

        var returned = orderDetails.paid - grandTotal
        if (returned < 0) returned = 0.0

        pdfBody.addView(
            getSubTotalRaw(
                getString(R.string.str_returned),
                String.format("$ %.2f", returned)
            ).apply {
                paddingTop = 20
                paddingBottom = 10
            }
        )


        var remaining = grandTotal - orderDetails.paid
        if (remaining < 0) remaining = 0.0

        pdfBody.addView(
            getSubTotalRaw(
                getString(R.string.str_remaining),
                String.format("$ %.2f", remaining)
            ).apply {
                paddingTop = 10
                paddingBottom = 10
            }
        )

        pdfBody.addView(
            PDFLineSeparatorView(
                applicationContext
            ).setBackgroundColor(Color.BLACK)
        )
        return pdfBody
    }

    private fun getSubTotalDetails(): PDFView {
        val column = PDFVerticalView(applicationContext)
        val subTotal = getSubTotalAmount()
        column.addView(
            getSubTotalRaw(
                getString(R.string.str_sub_total),
                String.format("$ %.2f", subTotal)
            )
        )
        var discount = 0.0
        var tax = 0.0
        if (orderDetails.discountPercentage > 0) {
            discount = subTotal * orderDetails.discountPercentage / 100
        }
        if (orderDetails.flatDiscount > 0) {
            discount += orderDetails.flatDiscount
        }
        if (discount > 0) {
            column.addView(
                getSubTotalRaw(
                    getString(R.string.str_discount),
                    String.format("$ %.2f", discount)
                )
            )
        }
        if (orderDetails.paymentMode == PaymentMode.CARD.value) {
            tax = (subTotal - discount) * 0.012
            column.addView(
                getSubTotalRaw(
                    getString(R.string.tax_surchange),
                    String.format("$ %.2f", tax)
                )
            )
        }
        if (orderDetails.advancePayment > 0) {
            column.addView(
                getSubTotalRaw(
                    getString(R.string.str_advance_payment),
                    "$ ${orderDetails.advancePayment}"
                )
            )
        }
        grandTotal = (subTotal + tax) - (discount + orderDetails.advancePayment)
        return column
    }

    private fun getSubTotalAmount(): Double {
        var total = 0.0
        orderDetails.items.forEach { item ->
            total += (item.qty * item.item!!.price)
        }
        return total
    }

    private fun getSubTotalRaw(title: String, value: String): PDFView {
        val subTotal = PDFHorizontalView(applicationContext)
        val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        item.text = SpannableString(title)
        item.setLayout(LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 0.85f
        ).apply {
            setMargins(0, 2, 0, 2)
        })
        subTotal.addView(item)
        val textAmount = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        textAmount.text = SpannableString(value)
        textAmount.setLayout(LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
        ).apply {
            setMargins(0, 2, 0, 2)
            gravity = Gravity.END
        })

        subTotal.addView(textAmount)

        return subTotal
    }

    private fun getOrderDetails(): PDFView {
        val column = PDFVerticalView(applicationContext)
        orderDetails.items.forEachIndexed { index, orderItem ->
            column.addView(getOrderItems(index, orderItem))
        }
        return column
    }

    override fun getFooterView(forPage: Int): PDFFooterView {
        val footerView = PDFFooterView(applicationContext)
        val pdfTextViewPage = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", forPage + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL

        footerView.addView(pdfTextViewPage)

        return footerView
    }

    override fun onShareClicked(savedPDFFile: File?) {
        val intentShareFile = Intent(
            Intent.ACTION_SEND
        )
        val apkURI = FileProvider.getUriForFile(
            applicationContext,
            applicationContext
                .packageName + ".fileprovider", savedPDFFile!!
        )
        intentShareFile.setDataAndType(
            apkURI,
            URLConnection.guessContentTypeFromName(savedPDFFile.name)
        )
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        intentShareFile.putExtra(
            Intent.EXTRA_STREAM,
            apkURI
        )

        startActivity(Intent.createChooser(intentShareFile, getString(R.string.str_share_file)))
    }


    private fun getCustomerRow(): PDFHorizontalView {
        val raw = PDFHorizontalView(applicationContext)
        val customerName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        customerName.text =
            SpannableString("${Constant.CUSTOMER_NAME} :   ${orderDetails.customerName}")
        customerName.setLayout(halfWeight.apply {
            setMargins(0, 10, 0, 10)
        })
        raw.addView(customerName)

        val paymentMode = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        paymentMode.text =
            SpannableString("${Constant.PAYMENT_MODE} :   ${orderDetails.paymentMode}")
        paymentMode.setLayout(halfWeight.apply {
            setMargins(0, 10, 0, 10)
        })
        raw.addView(paymentMode)
        return raw
    }

    private fun getHeaders(): PDFHorizontalView {
        val raw = PDFHorizontalView(applicationContext)
        headers.forEachIndexed { index, header ->
            when (index) {
                Headers.INDEX.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(header)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.10f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.ITEM.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(header)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.UNIT_PRICE.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(header)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.QTY.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(header)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }

                Headers.AMOUNT.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(header)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
            }

        }
        return raw
    }

    private fun getOrderItems(count: Int, orderItem: OrderItem): PDFHorizontalView {
        val raw = PDFHorizontalView(applicationContext)
        headers.forEachIndexed { index, _ ->
            when (index) {
                Headers.INDEX.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString("${count + 1}")
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.10f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.ITEM.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(orderItem.item!!.item)
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.UNIT_PRICE.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(String.format("$ %.2f", orderItem.item!!.price))
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
                Headers.QTY.value -> {
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(orderItem.qty.toString())
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }

                Headers.AMOUNT.value -> {
                    val amount = (orderItem.item!!.price * orderItem.qty)
                    val item = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
                    item.text = SpannableString(String.format("$ %.2f", amount))
                    item.setLayout(LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 0.15f
                    ).apply {
                        setMargins(0, 2, 0, 2)
                    })
                    raw.addView(item)
                }
            }

        }
        return raw
    }
}