/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.lockminds.tayari.datasource.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lockminds.tayari.model.Order
import com.lockminds.tayari.model.OrderItem
import com.lockminds.tayari.model.RemoteKeys
import com.lockminds.tayari.model.keys.OrderItemRemoteKeys
import com.lockminds.tayari.model.keys.OrderRemoteKeys

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderKeys(remoteKey: List<OrderRemoteKeys>)

    @Query("SELECT * FROM orders WHERE customer = :queryString ORDER BY id DESC")
    fun getOrders(queryString: String): PagingSource<Int, Order>

    @Query("DELETE FROM orders")
    suspend fun clearOrders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(repos: List<OrderItem>)

    @Query("SELECT * FROM order_items WHERE order_id = :queryString ORDER BY id DESC")
    fun getOrderItems(queryString: String): PagingSource<Int, OrderItem>

    @Query("DELETE FROM order_items WHERE order_id = :order")
    suspend fun clearOrderItems(order: String)

    @Query("DELETE FROM order_remote_keys")
    suspend fun clearKeysOrderItems()

    @Query("SELECT * FROM order_remote_keys WHERE id = :id")
    suspend fun remoteKeysRepoId(id: Long): OrderRemoteKeys?

    @Query("DELETE FROM order_remote_keys")
    suspend fun clearRemoteKeys()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemsKeys(remoteKey: List<OrderItemRemoteKeys>)

    @Query("SELECT * FROM order_items_remote_keys WHERE id = :id")
    suspend fun remoteKeysOrderItems(id: Long): OrderItemRemoteKeys?

    @Query("DELETE FROM order_items WHERE order_id = :id")
    suspend fun clearKeysOrderItems(id: String)

}