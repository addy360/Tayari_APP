package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.datasource.RestaurantDataSource
import com.lockminds.tayari.model.Restaurant

class ServiceDetailsViewModel(private val dataSource: RestaurantDataSource) : ViewModel() {


    /* Queries data source to returns a flower that corresponds to an id. */
    fun getDataForKey(key: String) : Restaurant? {
        return dataSource.getDataForKey(key)
    }

    /* Queries data source to remove a flower. */
    fun removeBusiness(news: Restaurant) {
        dataSource.remove(news)
    }
}

class ServiceDetailsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServiceDetailsViewModelFactory::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServiceDetailsViewModel(
                dataSource = RestaurantDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}