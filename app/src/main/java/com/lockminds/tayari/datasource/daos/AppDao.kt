package com.lockminds.tayari.datasource.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lockminds.tayari.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Start menu
    @Query("SELECT * FROM menu WHERE id = :key")
    fun getMenu(key: String): Menu

    @Query("SELECT * FROM menu_items WHERE id = :key")
    fun getMenuItem(key: String): MenuItem

    @Query("SELECT * FROM menu_items WHERE menu_id = :key")
    fun menuItems(key: String): List<MenuItem>

    // End menu

    // Start Cousins
    @Query("SELECT * FROM cousins")
    fun cousins() : Flow<List<Cousin>>

    @Query("SELECT * FROM menu WHERE cousin_id = :key")
    fun cousinMenu(key: String): List<Menu>
    // End Menu


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncCousins(cousins: List<Cousin>)

    @Query("SELECT * FROM cousins WHERE team_id = :key")
    fun restaurantCousins(key: String): List<Cousin>
    // End Cousins

    // Start Restaurants
    @Query("SELECT * FROM restaurants")
    fun restaurants() : List<Restaurant>

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants() : Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurants_near")
    fun getAllRestaurantsNear() : Flow<List<RestaurantNear>>

    @Query("SELECT * FROM restaurants WHERE " +
            "name LIKE :queryString OR address LIKE :queryString " +
            "ORDER BY name ASC")
    fun searchRestaurant(queryString: String): List<Restaurant>

    @Query("SELECT * FROM restaurants WHERE id = :key LIMIT 1")
    fun getRestaurant(key: String): Restaurant


    @Query("SELECT * FROM restaurants")
    fun getRestaurants() : Flow<List<Restaurant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncRestaurants(restaurant: List<Restaurant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncRestaurantsNear(restaurant: List<RestaurantNear>)
    // End Restaurants

    // Start Menu
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncMenu(cousins: List<Menu>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncMenuItems(cousins: List<MenuItem>)

    @Query("SELECT * FROM menu WHERE team_id = :key")
    fun restaurantMenu(key: String): List<Menu>
    // End Menu

}