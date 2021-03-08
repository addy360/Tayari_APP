package com.lockminds.tayari.datasource.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lockminds.tayari.datasource.tables.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg users: Users)

    @Query("SELECT * FROM users ")
    fun getUser(): Flow<Users>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

}