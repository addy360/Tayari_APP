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

package com.user.tayari.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.user.tayari.R
import com.user.tayari.viewHolders.RepoViewHolder
import com.user.tayari.viewHolders.SearchRestaurantViewHolder
import com.user.tayari.viewHolders.SeparatorViewHolder
import com.user.tayari.viewModels.UiModel
import com.user.tayari.viewModels.UiSearchModel

/**
 * Adapter for the list of repositories.
 */
class SearchRestaurantAdapter : PagingDataAdapter<UiSearchModel, ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.item_restaurants_all) {
            SearchRestaurantViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiSearchModel.RepoItem -> R.layout.item_restaurants_all
            is UiSearchModel.SeparatorItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiSearchModel.RepoItem -> (holder as SearchRestaurantViewHolder).bind(uiModel.repo)
                is UiSearchModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiSearchModel>() {
            override fun areItemsTheSame(oldItem: UiSearchModel, newItem: UiSearchModel): Boolean {
                return (oldItem is UiSearchModel.RepoItem && newItem is UiSearchModel.RepoItem &&
                        oldItem.repo.name == newItem.repo.name) ||
                        (oldItem is UiSearchModel.SeparatorItem && newItem is UiSearchModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiSearchModel, newItem: UiSearchModel): Boolean =
                    oldItem == newItem
        }
    }
}