package com.lockminds.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.model.Offer


/* Handles operations on flowersLiveData and holds details about it. */
class OffersDataSource() {

    private val initialOfferList = offersList()
    private val businessLiveData = MutableLiveData(initialOfferList)


    fun getOfferList(): LiveData<List<Offer>> {
        return businessLiveData
    }


    companion object {
        private var INSTANCE: OffersDataSource? = null

        fun getDataSource(): OffersDataSource {
            return synchronized(OffersDataSource::class) {
                val newInstance = INSTANCE ?: OffersDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}