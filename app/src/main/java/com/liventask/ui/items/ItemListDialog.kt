package com.liventask.ui.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.liventask.data.ItemModel
import com.liventask.databinding.AddOrderDialogLayoutBinding
import com.liventask.ui.items.adapter.ItemListAdapter

class ItemListDialog(private val listener: OnSubmitItems) : BottomSheetDialogFragment() {

    private lateinit var viewModel: ItemListVideModel
    private lateinit var binding: AddOrderDialogLayoutBinding
    private lateinit var itemAdapter: ItemListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddOrderDialogLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ItemListVideModel::class.java]
        itemAdapter = ItemListAdapter()
        binding.itemList.adapter = itemAdapter
        observeData()
    }

    private fun observeData() {
        viewModel.itemList.observe(this) {
            itemAdapter.updateData(it)
        }
        viewModel.getItemList()
        binding.btnSave.setOnClickListener {
            val selectedItem = itemAdapter.getSelectedData()
            listener.onSaveItems(selectedItem)
            this.dialog?.dismiss()
        }
    }

    interface OnSubmitItems {
        fun onSaveItems(items: ArrayList<ItemModel>)
    }

}