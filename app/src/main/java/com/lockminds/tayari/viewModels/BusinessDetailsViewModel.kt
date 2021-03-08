package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.datasource.BusinessDataSource



class BusinessDetailsViewModel(private val datasource: BusinessDataSource) : ViewModel() {


    /* Queries datasource to returns a flower that corresponds to an id. */
    fun getBusinessForKey(key: String) : Business? {
        return datasource.getBusinessForKey(key)
    }

    /* Queries datasource to remove a flower. */
    fun removeBusiness(news: Business) {
        datasource.removeBusiness(news)
    }
}

class BusinessDetailsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessDetailsViewModel(
                datasource = BusinessDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}