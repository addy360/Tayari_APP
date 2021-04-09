package com.lockminds.tayari.datasource.repository


import androidx.annotation.WorkerThread
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.datasource.daos.AppDao
import com.lockminds.tayari.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
        private val appDao: AppDao,
        private val database: AppDatabase) {

    // start menu
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun restaurantMenu(menu: String): List<Menu> = appDao.restaurantMenu(menu)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun cousinMenu(menu: String): List<Menu> = appDao.cousinMenu(menu)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getMenu(query: String): Menu = appDao.getMenu(query)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getMenuItem(query: String): MenuItem = appDao.getMenuItem(query)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun menuItems(query: String): List<MenuItem> = appDao.menuItems(query)

    // end menu

    //    Start cousins
    val cousins: Flow<List<Cousin>> = appDao.cousins()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun restaurantCousins(restaurant: String): List<Cousin> = appDao.restaurantCousins(restaurant)

    // End cousins

    // Start Restaurants
    val allRestaurants: Flow<List<Restaurant>> = appDao.getAllRestaurants()
    val allRestaurantsNear: Flow<List<RestaurantNear>> = appDao.getAllRestaurantsNear()

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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncRestaurantsNear(restaurant: List<RestaurantNear>){
        appDao.syncRestaurantsNear(restaurant)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncCousins(cousins: List<Cousin>){
        appDao.syncCousins(cousins)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncMenu(menu: List<Menu>){
        appDao.syncMenu(menu)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncMenuItems(items: List<MenuItem>){
        appDao.syncMenuItems(items)
    }
//    End Syncs

}