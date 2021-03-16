package com.lockminds.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.model.Restaurant


/* Handles operations on flowersLiveData and holds details about it. */
class RestaurantDataSource() {

    private val initialRestaurantList = businessList()
    private val businessLiveData = MutableLiveData(initialRestaurantList)


    fun getRestaurantList(): LiveData<List<Restaurant>> {
        return businessLiveData
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