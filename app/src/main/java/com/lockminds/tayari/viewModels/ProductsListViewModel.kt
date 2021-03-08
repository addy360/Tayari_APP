package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.data.Product
import com.lockminds.tayari.datasource.ProductsDataSource


class ProductsListViewModel(val productsDataSource: ProductsDataSource) : ViewModel() {

    val productsLiveData = productsDataSource.getProductsList()

    /* If the name and description are present, create new Service and add it to the datasource */
    fun insertProduct(product: Product) {
         productsDataSource.addProduct(product)
    }

    fun insertProductsList(product: List<Product>) {
        productsDataSource.addProductList(product)
    }

    fun refreshList() {
        productsDataSource.refreshProducts()
    }

}

class ProductsListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsListViewModel(
                    productsDataSource = ProductsDataSource.getDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}