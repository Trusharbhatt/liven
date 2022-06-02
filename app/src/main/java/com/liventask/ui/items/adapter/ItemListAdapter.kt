package com.liventask.ui.items.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.liventask.R
import com.liventask.data.ItemModel

class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {
    private var itemList = arrayListOf<ItemModel>()

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtItem: AppCompatTextView = itemView.findViewById(R.id.txtItem)
        private val txtItemPrice: AppCompatTextView = itemView.findViewById(R.id.txtItemPrice)
        val itemCheckBox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
        fun updateData(data: ItemModel) {
            txtItem.text = data.item
            txtItemPrice.text = "$ ${data.price}"
            itemCheckBox.isChecked = data.isChecked
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.updateData(itemList[position])
        holder.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
            itemList[position].isChecked = isChecked
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(data: ArrayList<ItemModel>) {
        itemList.clear()
        itemList.addAll(data)
        notifyDataSetChanged()
    }

    fun getSelectedData(): ArrayList<ItemModel> {
        val selectedItems = arrayListOf<ItemModel>()
        itemList.forEach { item ->
            if (item.isChecked) {
                selectedItems.apply {
                    add(item)
                }
            }
        }
        return selectedItems
    }
}