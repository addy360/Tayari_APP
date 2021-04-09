package com.lockminds.tayari.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.lockminds.tayari.datasource.RestaurantDataSource
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.model.RestaurantNear


class NearByViewModel(val businessDataSource: RestaurantDataSource, val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    val allNearBy: LiveData<List<RestaurantNear>> = repository.allRestaurantsNear.asLiveData()

}

class NearByViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NearByViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NearByViewModel(
                    businessDataSource = RestaurantDataSource.getDataSource(),
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}