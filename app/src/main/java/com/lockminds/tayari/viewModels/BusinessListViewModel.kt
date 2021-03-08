package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.datasource.BusinessDataSource


class BusinessListViewModel(val businessDataSource: BusinessDataSource) : ViewModel() {

    val businessLiveData = businessDataSource.getBusinessList()

    /* If the name and description are present, create new Service and add it to the datasource */
    fun insertBusiness(business: Business) {
         businessDataSource.addBusiness(business)
    }

    fun insertBusinessList(business: List<Business>) {
        businessDataSource.addBusinessList(business)
    }

}

class BusinessListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusinessListViewModel(
                    businessDataSource = BusinessDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}