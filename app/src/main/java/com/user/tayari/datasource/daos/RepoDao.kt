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

package com.user.tayari.datasource.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.user.tayari.model.RemoteKeys
import com.user.tayari.model.Restaurant
import com.user.tayari.model.RestaurantSearch
import com.user.tayari.model.keys.RestaurantSearchKeys

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Restaurant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRestSearch(repos: List<RestaurantSearch>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRestSearchKeys(repos: List<RestaurantSearchKeys>)

    @Query("SELECT * FROM restaurants ORDER BY name ASC")
    fun restaurants(): PagingSource<Int, Restaurant>

    @Query("SELECT * FROM restaurants_search ORDER BY name ASC")
    fun restaurantsSearch(): PagingSource<Int, RestaurantSearch>

    @Query("SELECT * FROM restaurants_search WHERE name LIKE :queryString ORDER BY name ASC")
    fun searchRest(queryString: String): PagingSource<Int, RestaurantSearch>

    @Query("DELETE FROM restaurants_search")
    suspend fun clearRestaurantsSearch()

    @Query("DELETE FROM restaurants")
    suspend fun clearRepos()

    @Query("DELETE FROM restaurant_search_key")
    suspend fun clearRestSearchKey()

    @Query("SELECT * FROM restaurant_search_key WHERE id = :repoId")
    suspend fun remoteKeysRestSearch(repoId: Long): RestaurantSearchKeys?

}