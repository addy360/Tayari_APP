package com.lockminds.tayari.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.lockminds.tayari.datasource.RestaurantDataSource
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.model.Restaurant


class OffersViewModel(val offersDataSource: RestaurantDataSource, val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    val allRestaurants: LiveData<List<Restaurant>> = repository.allRestaurants.asLiveData()

}

class OffersViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OffersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OffersViewModel(
                    offersDataSource = RestaurantDataSource.getDataSource(),
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}