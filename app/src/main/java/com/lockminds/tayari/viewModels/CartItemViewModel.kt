package com.lockminds.tayari.viewModels

import androidx.lifecycle.*
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.model.CartMenu


class CartItemViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    fun getMenu(id: String, type: String): LiveData<CartMenu>{
        return repository.getCartMenuFlow(id,type).asLiveData()
    }

}

class CartItemViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartItemViewModel(
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}