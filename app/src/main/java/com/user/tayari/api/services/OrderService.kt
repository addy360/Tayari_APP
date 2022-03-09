package com.user.tayari.api.services

import com.user.tayari.api.responses.OrderItemsResponse
import com.user.tayari.api.responses.OrdersResponse
import com.user.tayari.constants.APIURLs.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Order API communication setup via Retrofit.
 */
interface OrderService {

    /**
     * Get order ordered by id desc.
     */
    @GET("orders/get_orders")
    suspend fun searchOrders(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): OrdersResponse

    /**
     * Get order items
     */
    @GET("orders/get_order_items")
    suspend fun orderItems(
            @Header("Authorization") token: String,
            @Query("order") query: String,
            @Query("page") page: Int,
            @Query("per_page") itemsPerPage: Int
    ): OrderItemsResponse

    companion object {

        fun create(): OrderService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(OrderService::class.java)
        }
    }
}