package com.user.tayari.viewModels

import androidx.lifecycle.*
import com.user.tayari.datasource.RestaurantDataSource
import com.user.tayari.datasource.repository.AppRepository
import com.user.tayari.model.Restaurant
import com.user.tayari.model.RestaurantNear
import kotlinx.coroutines.flow.Flow


class RestaurantsViewModel(val businessDataSource: RestaurantDataSource, val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    val allRestaurants: LiveData<List<Restaurant>> = repository.allRestaurants.asLiveData()

}

class RestaurantsViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantsViewModel(
                    businessDataSource = RestaurantDataSource.getDataSource(),
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}