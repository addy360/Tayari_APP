/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lockminds.tayari.api

import android.content.Context
import com.lockminds.tayari.App
import com.lockminds.tayari.BuildConfig
import com.lockminds.tayari.constants.APIURLs.Companion.BASE_URL
import com.lockminds.tayari.constants.Constants
import com.lockminds.tayari.results.RestaurantsResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface RetrofitService {

    /**
     * Get restaurants ordered by id.
     */
    @GET("restaurants/get_all")
    suspend fun searchRepos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("perpage") itemsPerPage: Int,
    ): RestaurantsResponse

    companion object {

        fun create(): RetrofitService {

            val client = OkHttpClient.Builder()
                .apply {
                    if(BuildConfig.DEBUG){
                        val httpLoggingInterceptor = HttpLoggingInterceptor()
                        addInterceptor(httpLoggingInterceptor.apply {
                            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                        })
                    }
                }
                    .addInterceptor(getHeaderInterceptor())
                    .build()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RetrofitService::class.java)
        }

        private fun getHeaderInterceptor(): Interceptor {
            val preference = App.appContext.getSharedPreferences(
                Constants.PREFERENCE_KEY,
                Context.MODE_PRIVATE
            )
            return object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request =
                        chain.request().newBuilder()
                            .header(
                                "Authorization", "Bearer " + preference.getString(
                                    Constants.LOGIN_TOKEN,
                                    "false"
                                )
                            )
                            .header("Content-Type", "application/json")
                            .build()
                    return chain.proceed(request)
                }
            }
        }

    }
}