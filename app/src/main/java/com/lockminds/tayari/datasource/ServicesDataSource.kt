package com.lockminds.tayari.datasource

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.data.Service
import com.lockminds.tayari.data.servicesList


/* Handles operations on flowersLiveData and holds details about it. */
class ServicesDataSource() {
    private val initialServicesList = servicesList()
    private val servicesLiveData = MutableLiveData(initialServicesList)

    /* Adds service to liveData and posts value. */
    fun addFlower(service: Service) {
        val currentList = servicesLiveData.value
        if (currentList == null) {
            servicesLiveData.postValue(listOf(service))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, service)
            servicesLiveData.postValue(updatedList)
        }
    }

    /* Adds services to liveData and posts value. */
    fun addFlowers(services: List<Service>) {
        servicesLiveData.postValue(services)
    }

    /* Removes service from liveData and posts value. */
    fun removeFlower(service: Service) {
        val currentList = servicesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(service)
            servicesLiveData.postValue(updatedList)
        }
    }

    /* Removes service from liveData and posts value. */
    fun refreshFlowers() {
        val currentList = servicesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.clear()
            servicesLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getFlowerForKey(key: String): Service? {
        servicesLiveData.value?.let { flowers ->
            return flowers.firstOrNull{ it.service_key == key}
        }
        return null
    }


    fun getFlowerList(): LiveData<List<Service>> {
        return servicesLiveData
    }


    companion object {
        private var INSTANCE: ServicesDataSource? = null

        fun getDataSource(resources: Resources): ServicesDataSource {
            return synchronized(ServicesDataSource::class) {
                val newInstance = INSTANCE ?: ServicesDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}