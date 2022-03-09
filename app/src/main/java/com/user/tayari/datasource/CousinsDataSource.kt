package com.user.tayari.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.user.tayari.model.Cousin

class CousinDataSource() {

    private val initialList = cousinsList()
    private val listData = MutableLiveData(initialList)

    /* Adds single data to liveData and posts value. */
    fun add(cousin: Cousin) {
        val currentList = listData.value
        if (currentList == null) {
            listData.postValue(listOf(cousin))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, cousin)
            listData.postValue(updatedList)
        }
    }

    /* Add list data to liveData and posts value. */
    fun addList(list: List<Cousin>) {
        listData.postValue(list)
    }

    /* Removes single data from liveData and posts value. */
    fun remove(cousin: Cousin) {
        val currentList = listData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(cousin)
            listData.postValue(updatedList)
        }
    }

    /* Returns data given an ID. */
    fun getDataForKey(key: String): Cousin? {
        listData.value?.let { cousin ->
            return cousin.firstOrNull{  it.id.toString() == key}
        }
        return null
    }


    fun getList(): LiveData<List<Cousin>> {
        return listData
    }


    companion object {
        private var INSTANCE: CousinDataSource? = null

        fun getDataSource(): CousinDataSource {
            return synchronized(CousinDataSource::class) {
                val newInstance = INSTANCE ?: CousinDataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }


}