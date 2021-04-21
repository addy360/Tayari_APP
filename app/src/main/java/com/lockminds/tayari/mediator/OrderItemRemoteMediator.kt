package com.lockminds.tayari.mediator

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lockminds.tayari.SessionManager
import com.lockminds.tayari.api.services.OrderService
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.model.OrderItem
import com.lockminds.tayari.model.keys.OrderItemRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class OrderItemRemoteMediator(
    private val context: Context,
        private val query: String,
        private val service: OrderService,
        private val appDatabase: AppDatabase
) : RemoteMediator<Int, OrderItem>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, OrderItem>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                if (remoteKeys == null) {
                    // The LoadType is PREPEND so some data was loaded before,
                    // so we should have been able to get remote keys
                    // If the remoteKeys are null, then we're an invalid state and we have a bug
                    throw InvalidObjectException("Remote key and the prevKey should not be null")
                }
                // If the previous key is null, then we can't request more data
                val prevKey = remoteKeys.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys == null || remoteKeys.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextKey
            }

        }

        val apiQuery = query

        try {
            val sessionManager = SessionManager(context)
            val apiResponse = service.orderItems(token = "Bearer ${sessionManager.fetchAuthToken()}",apiQuery, page, state.config.pageSize)

            val items = apiResponse.items
            val endOfPaginationReached = items.isEmpty()
            appDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDatabase.orderDao().clearKeysOrderItems()
                    appDatabase.orderDao().clearOrderItems(query)
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = items.map {
                    OrderItemRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                appDatabase.orderDao().insertItemsKeys(keys)
                appDatabase.orderDao().insertItems(items)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, OrderItem>): OrderItemRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { item ->
                    // Get the remote keys of the last item retrieved
                    appDatabase.orderDao().remoteKeysOrderItems(item.id)
                }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, OrderItem>): OrderItemRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { order ->
                    // Get the remote keys of the first items retrieved
                    appDatabase.orderDao().remoteKeysOrderItems(order.id)
                }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
            state: PagingState<Int, OrderItem>
    ): OrderItemRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { itemId ->
                appDatabase.orderDao().remoteKeysOrderItems(itemId)
            }
        }
    }

}