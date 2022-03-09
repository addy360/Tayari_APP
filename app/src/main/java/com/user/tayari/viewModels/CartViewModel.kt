package com.user.tayari.viewModels

import androidx.lifecycle.*
import com.user.tayari.datasource.repository.AppRepository
import com.user.tayari.model.CartMenu


class CartViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    fun getMenus(id: String): LiveData<List<CartMenu>> = repository.getCartMenus(id).asLiveData()

}

class CartViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}