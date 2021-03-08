package com.lockminds.tayari.datasource

import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.data.ItemCategory
import com.lockminds.tayari.data.itemsCategoryList


/* Handles operations on flowersLiveData and holds details about it. */
class ItemsCategoryDataSource() {
    private val initialList = itemsCategoryList()
    private val ItemsCategoryLiveData = MutableLiveData(initialList)

    /* Adds ItemsCategory to liveData and posts value. */
    fun addItemCategory(ItemsCategory: ItemCategory) {
        val currentList = ItemsCategoryLiveData.value
        if (currentList == null) {
            ItemsCategoryLiveData.postValue(listOf(ItemsCategory))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, ItemsCategory)
            ItemsCategoryLiveData.postValue(updatedList)
        }
    }

    /* Adds ItemsCategory to liveData and posts value. */
    fun addItemCategoryList(ItemsCategory: List<ItemCategory>) {
        ItemsCategoryLiveData.postValue(ItemsCategory)
    }

    /* Removes ItemsCategory from liveData and posts value. */
    fun removeItemCategory(ItemsCategory: ItemCategory) {
        val currentList = ItemsCategoryLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(ItemsCategory)
            ItemsCategoryLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getItemCategoryForKey(key: String): ItemCategory? {
        ItemsCategoryLiveData.value?.let { flowers ->
            return flowers.firstOrNull{ it.business_key == key}
        }
        return null
    }


    fun getItemCategoryList(): MutableLiveData<List<ItemCategory>> {
        return ItemsCategoryLiveData
    }


    companion object {
        private var INSTANCE: ItemsCategoryDataSource? = null

        fun getDataSource(): ItemsCategoryDataSource {
            return synchronized(ItemsCategoryDataSource::class) {
                val newInstance = INSTANCE ?: ItemsCategoryDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}