package com.lockminds.tayari.datasource.daos

import androidx.room.*
import com.lockminds.tayari.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Tables
    @Query("SELECT * FROM tables WHERE team_id = :id")
    fun getTables(id: String): Flow<List<Table>>
    // End Orders

    // Orders
    @Query("SELECT * FROM orders WHERE customer = :id ORDER BY id DESC")
    fun getOrders(id: String): Flow<List<Order>>

    // Orders
    @Query("SELECT * FROM orders WHERE customer = :customer AND id = :order")
    fun getOrder(customer: String, order:String): Order

    @Query("SELECT * FROM cart_menu LIMIT 1")
    fun getOneCart(): CartMenu

    // End Orders

    // Start Cart
    @Query("SELECT * FROM cart_items WHERE id = :key")
    fun getCartItem(key: String): CartItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCartItem(item: CartItem)

    @Query("SELECT * FROM cart_menu WHERE id = :id AND type = :type")
    fun getCartMenu(id: String, type: String): CartMenu

    @Query("SELECT * FROM cart_menu WHERE id = :id AND type = :type")
    fun getCartMenuFlow(id: String, type: String): Flow<CartMenu>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCartMenu(menu: CartMenu)

    @Query("DELETE FROM cart_menu WHERE id = :id AND type = :type")
    fun deleteCartMenu(id: String, type: String)

    @Query("DELETE FROM cart_items WHERE id = :id")
    fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items WHERE team_id = :id")
    fun emptyCartItem(id: String)


    @Query("DELETE FROM cart_menu WHERE team_id = :id")
    fun emptyCart(id: String)

    @Query("SELECT * FROM cart_menu WHERE team_id = :id")
    fun getCartMenus(id: String): Flow<List<CartMenu>>

    @Query("SELECT * FROM cart_menu WHERE team_id = :id")
    fun getCartMenusList(id: String): List<CartMenu>

    @Query("SELECT * FROM cart_items WHERE menu_id = :key")
    fun getCartItems(key: String): List<CartItem>
    // End Cart

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

    @Query("DELETE FROM cousins")
    fun emptyCousins()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertCousins(items: List<Cousin>)

    @Transaction
    fun syncCousins(restaurant: List<Cousin>) {
        // Anything inside this method runs in a single transaction.
        emptyCousins()
        insertCousins(restaurant)
    }

    @Query("SELECT * FROM menu WHERE cousin_id = :key")
    fun cousinMenu(key: String): List<Menu>
    // End Menu


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

    @Query("DELETE FROM restaurants")
    fun emptyRestaurants()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurants(restaurant: List<Restaurant>)

    @Query("DELETE FROM restaurants_near")
    fun emptyRestaurantsNear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestaurantsNear(restaurant: List<RestaurantNear>)


    @Transaction
    fun syncRestaurantsNear(restaurant: List<RestaurantNear>) {
        // Anything inside this method runs in a single transaction.
        emptyRestaurantsNear()
        insertRestaurantsNear(restaurant)
    }

    @Transaction
    fun syncRestaurants(restaurant: List<Restaurant>) {
        // Anything inside this method runs in a single transaction.
        emptyRestaurants()
        insertRestaurants(restaurant)
    }

    // End Restaurants

    // Start Menu

    @Query("DELETE FROM menu WHERE team_id = :id")
    fun emptyMenuItems(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMenuItems(cousins: List<MenuItem>)

    @Transaction
    fun syncMenuItems(id: String,items: List<MenuItem>){
        emptyMenuItems(id)
        insertMenuItems(items)
    }

    @Query("DELETE FROM menu WHERE team_id = :id")
    fun emptyMenu(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun  insertMenu(items: List<Menu>)

    @Transaction
    fun syncMenu(id: String,items: List<Menu>) {
        // Anything inside this method runs in a single transaction.
        emptyMenu(id)
        insertMenu(items)
    }

    @Query("SELECT * FROM menu WHERE team_id = :key")
    fun restaurantMenu(key: String): List<Menu>

    @Query("DELETE FROM orders")
    fun emptyOrders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<Order>)


    @Transaction
    fun syncOrders(orders: List<Order>){
        emptyOrders()
        insertOrders(orders)
    }


    // Tables
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun syncTables(orders: List<Table>)

}