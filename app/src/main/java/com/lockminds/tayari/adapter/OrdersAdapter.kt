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

package com.lockminds.tayari.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lockminds.tayari.R
import com.lockminds.tayari.viewHolders.OrderViewHolder
import com.lockminds.tayari.viewHolders.SeparatorViewHolder
import com.lockminds.tayari.viewModels.OrderUiModel

/**
 * Adapter for the list of repositories.
 */
class OrdersAdapter : PagingDataAdapter<OrderUiModel, ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.item_order) {
            OrderViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderUiModel.OrderItem -> R.layout.item_order
            is OrderUiModel.StatusItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is OrderUiModel.OrderItem -> (holder as OrderViewHolder).bind(uiModel.order)
                is OrderUiModel.StatusItem -> (holder as SeparatorViewHolder).bind(uiModel.status)
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<OrderUiModel>() {
            override fun areItemsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean {
                return (oldItem is OrderUiModel.OrderItem && newItem is OrderUiModel.OrderItem &&
                        oldItem.order.id == newItem.order.id) ||
                        (oldItem is OrderUiModel.StatusItem && newItem is OrderUiModel.StatusItem &&
                                oldItem.status == newItem.status)
            }

            override fun areContentsTheSame(oldItem: OrderUiModel, newItem: OrderUiModel): Boolean =
                    oldItem == newItem
        }
    }
}