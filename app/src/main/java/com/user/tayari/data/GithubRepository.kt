package com.user.tayari.data

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.user.tayari.api.GithubService
import com.user.tayari.datasource.AppDatabase
import com.user.tayari.mediator.RestaurantSearchMediator
import com.user.tayari.model.Restaurant
import com.user.tayari.model.RestaurantSearch
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with local and remote data sources.
 */

class GithubRepository(
        private val service: GithubService,
        private val database: AppDatabase
) {

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    @ExperimentalPagingApi
    fun restaurants(context: Context): Flow<PagingData<Restaurant>> {

        val pagingSourceFactory = { database.reposDao().restaurants() }

        val query: String = ""

        return Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
                remoteMediator = GithubRemoteMediator(
                        context,
                        query,
                        service,
                        database
                ),
                pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    @ExperimentalPagingApi
    fun searchRest(context: Context,query: String): Flow<PagingData<RestaurantSearch>> {

        val pagingSourceFactory = { database.reposDao().restaurantsSearch() }

//        if(query.isNotEmpty()){
//            pagingSourceFactory = { database.reposDao().searchRest(query) }
//        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RestaurantSearchMediator(
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
