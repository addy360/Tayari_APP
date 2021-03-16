package com.lockminds.tayari.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.datasource.daos.AppDao
import com.lockminds.tayari.datasource.daos.RemoteKeysDao
import com.lockminds.tayari.datasource.tables.RemoteKeys
import com.lockminds.tayari.datasource.tables.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Users::class,RemoteKeys::class, Restaurant::class], version = 2, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
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
                    "com.lockminds.tayari_database"
                ).addCallback(AgripoaDatabaseCallback(scope))
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
                        AppDatabase::class.java, "com.lockminds.tayari_database")
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