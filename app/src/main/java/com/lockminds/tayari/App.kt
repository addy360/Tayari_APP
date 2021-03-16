package com.lockminds.tayari

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.datasource.repository.AppRepository
import com.androidnetworking.AndroidNetworking
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.lockminds.tayari.api.RetrofitService
import com.lockminds.tayari.constants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class App: Application() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var retrofitService: RetrofitService
    private val database by lazy { AppDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { AppRepository(database.appDao(),database) }

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
        appContext = applicationContext
    }

    companion object {

        lateinit  var appContext: Context


    }

}