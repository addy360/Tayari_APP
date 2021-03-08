package com.lockminds.tayari.datasource.repository


import androidx.annotation.WorkerThread
import com.lockminds.tayari.datasource.daos.AppDao
import com.lockminds.tayari.datasource.tables.Users
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val user: Flow<Users> = appDao.getUser()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(user: Users) {
        appDao.insertUser(user)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllUsers() {
        appDao.deleteAllUsers()
    }

}