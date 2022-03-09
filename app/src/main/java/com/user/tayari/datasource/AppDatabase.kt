package com.user.tayari.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.user.tayari.datasource.daos.RemoteKeysDao
import com.user.tayari.datasource.daos.AppDao
import com.user.tayari.datasource.daos.RepoDao
import com.user.tayari.datasource.daos.OrderDao
import com.user.tayari.model.*
import com.user.tayari.model.keys.OrderItemRemoteKeys
import com.user.tayari.model.keys.OrderRemoteKeys
import com.user.tayari.model.keys.RestaurantSearchKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    RemoteKeys::class,
    RestaurantNear::class, Restaurant::class,
    Cousin::class, CartMenu::class,
    CartItem::class, Order::class,
    OrderItem::class, Menu::class,
    MenuItem::class, Table::class,
    OrderRemoteKeys::class,OrderItemRemoteKeys::class,
    RestaurantSearch::class, RestaurantSearchKeys::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
    abstract fun reposDao(): RepoDao
    abstract fun orderDao(): OrderDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context,scope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "com.user.tayari_database"
                ).addCallback(AgripoaDatabaseCallback(scope))
                        .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }

        }

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "com.user.tayari_database")
                    .fallbackToDestructiveMigration()
                        .build()

    }

    private class AgripoaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch { }
            }
        }

    }

}