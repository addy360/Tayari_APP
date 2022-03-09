package com.user.tayari.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.user.tayari.datasource.RestaurantDataSource
import com.user.tayari.datasource.repository.AppRepository


class ItemsCategoryListViewModel(private val appRepository: AppRepository, val dataSource: RestaurantDataSource) : ViewModel() {

    private val repository = appRepository
    val itemsCategoryLiveData = repository.allRestaurants.asLiveData()

}

class ItemsCategoryListViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsCategoryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsCategoryListViewModel(
                    appRepository = appRepository,
                    dataSource = RestaurantDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}