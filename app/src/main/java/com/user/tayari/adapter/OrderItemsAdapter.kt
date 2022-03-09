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
import com.user.tayari.viewHolders.OrderItemViewHolder
import com.user.tayari.viewHolders.OrderViewHolder
import com.user.tayari.viewHolders.SeparatorViewHolder
import com.user.tayari.viewModels.OrderItemUiModel
import com.user.tayari.viewModels.OrderUiModel

/**
 * Adapter for the list of repositories.
 */
class OrderItemsAdapter : PagingDataAdapter<OrderItemUiModel, ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == R.layout.item_order_item) {
            OrderItemViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderItemUiModel.OrderDataItem -> R.layout.item_order_item
            is OrderItemUiModel.StatusItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is OrderItemUiModel.OrderDataItem -> (holder as OrderItemViewHolder).bind(uiModel.order)
                is OrderItemUiModel.StatusItem -> (holder as SeparatorViewHolder).bind(uiModel.status)
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<OrderItemUiModel>() {
            override fun areItemsTheSame(oldItem: OrderItemUiModel, newItem: OrderItemUiModel): Boolean {
                return (oldItem is OrderItemUiModel.OrderDataItem && newItem is OrderItemUiModel.OrderDataItem &&
                        oldItem.order.id == newItem.order.id) ||
                        (oldItem is OrderItemUiModel.StatusItem && newItem is OrderItemUiModel.StatusItem &&
                                oldItem.status == newItem.status)
            }

            override fun areContentsTheSame(oldItem: OrderItemUiModel, newItem: OrderItemUiModel): Boolean =
                    oldItem == newItem
        }
    }
}