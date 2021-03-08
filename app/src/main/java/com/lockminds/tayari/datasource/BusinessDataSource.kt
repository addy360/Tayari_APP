package com.lockminds.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.data.businessList


/* Handles operations on flowersLiveData and holds details about it. */
class BusinessDataSource() {
    private val initialBusinessList = businessList()
    private val businessLiveData = MutableLiveData(initialBusinessList)

    /* Adds business to liveData and posts value. */
    fun addBusiness(business: Business) {
        val currentList = businessLiveData.value
        if (currentList == null) {
            businessLiveData.postValue(listOf(business))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, business)
            businessLiveData.postValue(updatedList)
        }
    }

    /* Adds business to liveData and posts value. */
    fun addBusinessList(business: List<Business>) {
        businessLiveData.postValue(business)
    }

    /* Removes business from liveData and posts value. */
    fun removeBusiness(business: Business) {
        val currentList = businessLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(business)
            businessLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getBusinessForKey(key: String): Business? {
        businessLiveData.value?.let { flowers ->
            return flowers.firstOrNull{ it.business_key == key}
        }
        return null
    }


    fun getBusinessList(): LiveData<List<Business>> {
        return businessLiveData
    }


    companion object {
        private var INSTANCE: BusinessDataSource? = null

        fun getDataSource(): BusinessDataSource {
            return synchronized(BusinessDataSource::class) {
                val newInstance = INSTANCE ?: BusinessDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}