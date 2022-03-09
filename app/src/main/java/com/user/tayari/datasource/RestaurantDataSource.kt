package com.user.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.user.tayari.model.Restaurant


/* Handles operations on flowersLiveData and holds details about it. */
class RestaurantDataSource() {

    private val initialList = restaurantsListEmpty()
    private val listData = MutableLiveData(initialList)

    /* Adds single data to liveData and posts value. */
    fun add(business: Restaurant) {
        val currentList = listData.value
        if (currentList == null) {
            listData.postValue(listOf(business))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, business)
            listData.postValue(updatedList)
        }
    }

    /* Add list data to liveData and posts value. */
    fun addList(list: List<Restaurant>) {
        listData.postValue(list)
    }

    /* Removes single data from liveData and posts value. */
    fun remove(business: Restaurant) {
        val currentList = listData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(business)
            listData.postValue(updatedList)
        }
    }

    /* Returns data given an ID. */
    fun getDataForKey(key: String): Restaurant? {
        listData.value?.let { flowers ->
            return flowers.firstOrNull{  it.id.toString() == key}
        }
        return null
    }


    fun getList(): LiveData<List<Restaurant>> {
        return listData
    }


    companion object {
        private var INSTANCE: RestaurantDataSource? = null

        fun getDataSource(): RestaurantDataSource {
            return synchronized(RestaurantDataSource::class) {
                val newInstance = INSTANCE ?: RestaurantDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
    

}