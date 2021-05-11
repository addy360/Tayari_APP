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

package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.lockminds.tayari.data.GithubRepository
import com.lockminds.tayari.model.Restaurant
import com.lockminds.tayari.model.RestaurantSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ViewModel for the [RestaurantsActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
class SearchRestaurantViewModel(private val repository: GithubRepository) : ViewModel() {
    private var currentQueryValue: String? = null

    private var currentSearchResult: Flow<PagingData<UiSearchModel>>? = null


    @ExperimentalPagingApi
    fun searchRest(context: Context,queryString: String): Flow<PagingData<UiSearchModel>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<UiSearchModel>> = repository.searchRest(context,queryString)
            .map { pagingData -> pagingData.map { UiSearchModel.RepoItem(it) } }
            .map {
                it.insertSeparators<UiSearchModel.RepoItem, UiSearchModel> { before, after ->
                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    if (before == null) {
                        // we're at the beginning of the list
                        return@insertSeparators UiSearchModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                    }
                    // check between 2 items
                    if (before.roundedStarCount > after.roundedStarCount) {
                        if (after.roundedStarCount >= 1) {
                            UiSearchModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        } else {
                            UiSearchModel.SeparatorItem("< 10.000+ stars")
                        }
                    } else {
                        // no separator
                        null
                    }
                }
            }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    @ExperimentalPagingApi
    fun restaurants(query:String,context: Context): Flow<PagingData<UiSearchModel>> {

        val newResult: Flow<PagingData<UiSearchModel>> = repository.searchRest(context,query)
                .map { pagingData -> pagingData.map { UiSearchModel.RepoItem(it) } }
                .map {
                    it.insertSeparators<UiSearchModel.RepoItem, UiSearchModel> { before, after ->
                        if (after == null) {
                            // we're at the end of the list
                            return@insertSeparators null
                        }

                        if (before == null) {
                            // we're at the beginning of the list
                            return@insertSeparators UiSearchModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                        }
                        // check between 2 items
                        if (before.roundedStarCount > after.roundedStarCount) {
                            if (after.roundedStarCount >= 1) {
                                UiSearchModel.SeparatorItem("${after.roundedStarCount}0.000+ stars")
                            } else {
                                UiSearchModel.SeparatorItem("< 10.000+ stars")
                            }
                        } else {
                            // no separator
                            null
                        }
                    }
                }
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }
}

sealed class UiSearchModel {
    data class RepoItem(val repo: RestaurantSearch) : UiSearchModel()
    data class SeparatorItem(val description: String) : UiSearchModel()
}

private val UiSearchModel.RepoItem.roundedStarCount: Int
    get() = this.repo.id.toInt() / 10_000

class SearchRestaurantViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchRestaurantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchRestaurantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}