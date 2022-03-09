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

package com.user.tayari.datasource.repository

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.user.tayari.api.services.OrderService
import com.user.tayari.datasource.AppDatabase
import com.user.tayari.mediator.OrderItemRemoteMediator
import com.user.tayari.mediator.OrderRemoteMediator
import com.user.tayari.model.Order
import com.user.tayari.model.OrderItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with local and remote data sources.
 */

class OrderRepository(
        private val service: OrderService,
        private val database: AppDatabase
) {

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    @ExperimentalPagingApi
    fun getOrders(context: Context,query: String): Flow<PagingData<Order>> {

        val pagingSourceFactory = { database.orderDao().getOrders(query) }

        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = OrderRemoteMediator(
                        context,
                        query,
                        service,
                        database
                ),
                pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    @ExperimentalPagingApi
    fun getOrderItems(context: Context,query: String): Flow<PagingData<OrderItem>> {

        val pagingSourceFactory = { database.orderDao().getOrderItems(query) }

        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = OrderItemRemoteMediator(
                        context,
                        query,
                        service,
                        database
                ),
                pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}
