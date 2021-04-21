package com.lockminds.tayari.datasource.repository


import androidx.annotation.WorkerThread
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.datasource.daos.AppDao
import com.lockminds.tayari.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(
        private val appDao: AppDao,
        private val database: AppDatabase) {

    // Tables
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getTables(id: String): Flow<List<Table>> = appDao.getTables(id)
    // End Tables

    // Orders
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getOrders(id: String): Flow<List<Order>> = appDao.getOrders(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getOneCart(): CartMenu = appDao.getOneCart()
    // End Orders

//    Start cart

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        suspend fun getCartItems(menu: String): List<CartItem> = appDao.getCartItems(menu)

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        fun getCartMenus(id: String): Flow<List<CartMenu>> = appDao.getCartMenus(id)

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        fun getCartMenusList(id: String): List<CartMenu> = appDao.getCartMenusList(id)

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        suspend fun getCartMenu(id: String, type: String): CartMenu = appDao.getCartMenu(id,type)

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        fun getCartMenuFlow(id: String, type: String): Flow<CartMenu> = appDao.getCartMenuFlow(id,type)

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        suspend fun getCartItem(query: String): CartItem = appDao.getCartItem(query)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addCartItem(item: CartItem){
        val menuItem = appDao.getCartItem(item.id.toString())
        if(menuItem != null){
            menuItem.qty = (menuItem.qty.toString().toFloat() + 1).toString()
            menuItem.total = (menuItem.price.toString().toFloat() * menuItem.qty.toString().toFloat()).toString()
            appDao.addCartItem(menuItem)
        }else{
            appDao.addCartItem(item)
        }
    }

        @Suppress("RedundantSuspendModifier")
        @WorkerThread
        suspend fun addCartMenu(item: CartMenu){
            val menuItem = appDao.getCartMenu(item.id.toString(),item.type.toString())
            if(menuItem != null){
                menuItem.qty = (menuItem.qty.toString().toFloat() + 1).toString()
                menuItem.total = (menuItem.price.toString().toFloat() * menuItem.qty.toString().toFloat()).toString()
                appDao.addCartMenu(menuItem)
            }else{
                appDao.addCartMenu(item)
            }
        }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lowerCartMenu(item: CartMenu){
        val menuItem = appDao.getCartMenu(item.id.toString(),item.type.toString())
        if(menuItem != null){
            menuItem.qty = (menuItem.qty.toString().toFloat() * 1 - 1).toString()
            menuItem.total = (menuItem.price.toString().toFloat() * menuItem.qty.toString().toFloat()).toString()
            if(menuItem.qty != null){
                val qty = menuItem.qty.toString().toFloat()
                if (qty <= 0){
                    appDao.deleteCartMenu(item.id.toString(),item.type.toString())
                }else{
                    appDao.addCartMenu(menuItem)
                }
            }

        }

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun lowerCartItem(item: CartItem){
        val cartItem = appDao.getCartItem(item.id.toString())
        if(cartItem != null){
            cartItem.qty = (cartItem.qty.toString().toFloat() * 1 - 1).toString()
            cartItem.total = (cartItem.price.toString().toFloat() * cartItem.qty.toString().toFloat()).toString()
            if(cartItem.qty != null){
                val qty = cartItem.qty.toString().toFloat()
                if (qty <= 0){
                    appDao.deleteCartItem(item.id.toString())
                }else{
                    appDao.addCartItem(cartItem)
                }
            }

        }

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeCartItem(item: CartItem){
        appDao.deleteCartItem(item.id.toString())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeCartMenu(item: CartMenu){
        appDao.deleteCartMenu(item.id.toString(),item.type.toString())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyCartItem(id: String){
        appDao.emptyCartItem(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyCart(id: String){
        appDao.emptyCart(id)
    }

//    End Cart

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
    suspend fun syncMenu(id: String,menu: List<Menu>){
        appDao.syncMenu(id,menu)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncMenuItems(id: String,items: List<MenuItem>){
        appDao.syncMenuItems(id,items)
    }



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun syncTables(items: List<Table>){
        appDao.syncTables(items)
    }

//    End Syncs

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

}