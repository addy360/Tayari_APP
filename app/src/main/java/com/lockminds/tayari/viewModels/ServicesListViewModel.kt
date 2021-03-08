package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.Service
import com.lockminds.tayari.datasource.ServicesDataSource


class ServicesListViewModel(val servicesDataSource: ServicesDataSource) : ViewModel() {

    val servicesLiveData = servicesDataSource.getFlowerList()

    fun insertService(service: Service) {
         servicesDataSource.addFlower(service)
    }

    fun insertServices(services: List<Service>) {
        servicesDataSource.addFlowers(services)
    }

    fun refreshList() {
        servicesDataSource.refreshFlowers()
    }

}

class ServicesListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServicesListViewModel(
                    servicesDataSource = ServicesDataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}