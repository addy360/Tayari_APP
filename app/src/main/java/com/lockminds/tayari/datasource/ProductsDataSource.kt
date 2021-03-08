package com.lockminds.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lockminds.tayari.data.Product
import com.lockminds.tayari.data.productsList


/* Handles operations on flowersLiveData and holds details about it. */
class ProductsDataSource() {
    private val initialProductList = productsList()
    private val productLiveData = MutableLiveData(initialProductList)

    /* Adds product to liveData and posts value. */
    fun addProduct(product: Product) {
        val currentList = productLiveData.value
        if (currentList == null) {
            productLiveData.postValue(listOf(product))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, product)
            productLiveData.postValue(updatedList)
        }
    }

    /* Adds product to liveData and posts value. */
    fun addProductList(product: List<Product>) {
        productLiveData.postValue(product)
    }

    /* Removes service from liveData and posts value. */
    fun refreshProducts() {
        val currentList = productLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.clear()
            productLiveData.postValue(updatedList)
        }
    }

    /* Removes product from liveData and posts value. */
    fun removeProduct(product: Product) {
        val currentList = productLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(product)
            productLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getProductForKey(key: String): Product? {
        productLiveData.value?.let { flowers ->
            return flowers.firstOrNull{ it.product_key == key}
        }
        return null
    }


    fun getProductsList(): LiveData<List<Product>> {
        return productLiveData
    }


    companion object {
        private var INSTANCE: ProductsDataSource? = null

        fun getDataSource(): ProductsDataSource {
            return synchronized(ProductsDataSource::class) {
                val newInstance = INSTANCE ?: ProductsDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}