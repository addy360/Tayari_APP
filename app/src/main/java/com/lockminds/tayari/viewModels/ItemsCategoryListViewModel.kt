package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.ItemCategory
import com.lockminds.tayari.datasource.ItemsCategoryDataSource


class ItemsCategoryListViewModel(val ItemsCategoryDataSource: ItemsCategoryDataSource) : ViewModel() {

    val ItemsCategoryLiveData = ItemsCategoryDataSource.getItemCategoryList()

    /* If the name and description are present, create new Service and add it to the datasource */
    fun insertBusiness(ItemsCategory: ItemCategory) {
         ItemsCategoryDataSource.addItemCategory(ItemsCategory)
    }

    fun insertBusinessList(ItemsCategory: List<ItemCategory>) {
        ItemsCategoryDataSource.addItemCategoryList(ItemsCategory)
    }

}

class ItemsCategoryListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsCategoryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsCategoryListViewModel(
                    ItemsCategoryDataSource = ItemsCategoryDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}