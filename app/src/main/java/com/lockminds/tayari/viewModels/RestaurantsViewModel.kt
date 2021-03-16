package com.lockminds.tayari.viewModels

import androidx.lifecycle.*
import com.lockminds.tayari.datasource.RestaurantDataSource
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.model.Restaurant
import kotlinx.coroutines.flow.Flow


class RestaurantsViewModel(val businessDataSource: RestaurantDataSource, val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    private val query = MutableLiveData<String>()

    val allRestaurants: LiveData<List<Restaurant>> = repository.allRestaurants.asLiveData()

    val results: LiveData<List<Restaurant>> = Transformations.map(
            query,
            ::temp
    )

    private fun temp(query: String) = repository.searchRestaurant(query)
    fun searchRestaurant(name: String) = apply { query.postValue(name) }

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