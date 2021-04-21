package com.lockminds.tayari.viewModels

import androidx.lifecycle.*
import com.lockminds.tayari.datasource.repository.AppRepository
import com.lockminds.tayari.model.CartMenu
import com.lockminds.tayari.model.Order
import com.lockminds.tayari.model.Table


class TablesViewModel(private val appRepository: AppRepository) : ViewModel() {

    private val repository = appRepository

    fun getTables(id: String): LiveData<List<Table>>{
        return repository.getTables(id).asLiveData()
    }

}

class TablesViewModelFactory(private val appRepository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TablesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TablesViewModel(
                    appRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}