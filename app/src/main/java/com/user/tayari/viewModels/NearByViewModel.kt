package com.user.tayari.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.user.tayari.datasource.RestaurantDataSource
import com.user.tayari.datasource.repository.AppRepository
import com.user.tayari.model.Restaurant
import com.user.tayari.model.RestaurantNear


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