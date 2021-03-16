package com.lockminds.tayari.datasource.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lockminds.tayari.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Start Restaurants
    @Query("SELECT * FROM restaurant")
    fun restaurants() : List<Restaurant>

    @Query("SELECT * FROM restaurant")
    fun getAllRestaurants() : Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurant WHERE " +
            "business_name LIKE :queryString OR business_location LIKE :queryString OR business_address LIKE :queryString " +
            "ORDER BY business_name ASC")
    fun searchRestaurant(queryString: String): List<Restaurant>

    @Query("SELECT * FROM restaurant WHERE business_key = :key LIMIT 1")
    fun getRestaurant(key: String): Restaurant


    @Query("SELECT * FROM restaurant")
    fun getRestaurants() : Flow<List<Restaurant>>

    // End Restaurants

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncRestaurants(restaurant: List<Restaurant>)

}