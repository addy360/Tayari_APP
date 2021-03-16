package com.lockminds.tayari.datasource.repository


import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import androidx.room.Query
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.datasource.daos.AppDao
import kotlinx.coroutines.flow.Flow

class AppRepository(
        private val appDao: AppDao,
        private val database: AppDatabase) {

    // Start Restaurants

    val allRestaurants: Flow<List<Restaurant>> = appDao.getAllRestaurants()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun restaurants(): List<Restaurant> = appDao.restaurants()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getRestaurant(query: String): Restaurant = appDao.getRestaurant(query)


    fun searchRestaurant(query: String) : List<Restaurant>{
        return appDao.searchRestaurant(query)
    }

    // End Restaurants


//    Start Syncs
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncRestaurants(restaurant: List<Restaurant>){
        appDao.syncRestaurants(restaurant)
    }
//    End Syncs

}