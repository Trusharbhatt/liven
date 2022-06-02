package com.liventask.ui.items

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liventask.data.AppData
import com.liventask.data.ItemModel

class ItemListVideModel : ViewModel() {
    val itemList = MutableLiveData<ArrayList<ItemModel>>()
    fun getItemList() {
        viewModelScope.launch {
            /**** here we can data from database or network for now I am using static data as per task.*/
            itemList.postValue(AppData.itemList.value)
        }
    }
}