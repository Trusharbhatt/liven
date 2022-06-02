package com.liventask.ui.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.liventask.R
import com.liventask.data.ItemModel
import com.liventask.data.OrderItem

class OrderListAdapter : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {
    private var orderList = arrayListOf<OrderItem>()

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtQty: AppCompatTextView = itemView.findViewById(R.id.txtQty)
        private val txtItem: AppCompatTextView = itemView.findViewById(R.id.txtItem)
        private val txtItemPrice: AppCompatTextView = itemView.findViewById(R.id.txtItemPrice)
        val btnPlus: MaterialButton = itemView.findViewById(R.id.btnPlus)
        val btnMinus: MaterialButton = itemView.findViewById(R.id.btnMinus)
        val btnDelete: AppCompatImageButton = itemView.findViewById(R.id.btnDelete)
        fun updateData(data: OrderItem) {
            txtQty.text = data.qty.toString()
            txtItem.text = data.item?.item
            txtItemPrice.text = "$ ${data.item?.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_item_layout, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.updateData(orderList[position])
        holder.btnPlus.setOnClickListener {
            orderList[position].qty++
            notifyDataSetChanged()
        }

        holder.btnDelete.setOnClickListener {
            orderList.removeAt(position)
            notifyDataSetChanged()
        }

        holder.btnMinus.setOnClickListener {
            if (orderList[position].qty > 0) {
                orderList[position].qty--
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun addItems(data: ArrayList<ItemModel>) {
        val newData = arrayListOf<OrderItem>()
        data.forEach { item ->
            newData.add(OrderItem(item, 1))
        }
        orderList.addAll(newData)
        notifyDataSetChanged()
    }

    fun getAllItems(): ArrayList<OrderItem> {
        return orderList
    }


}