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

package com.user.tayari.viewHolders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.user.tayari.Tools
import com.user.tayari.model.Order
import com.user.tayari.model.OrderItem
import user.tayari.R
import java.util.*

/**
 * View Holder for a [Order] RecyclerView list item.
 */
class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val name: TextView = view.findViewById(R.id.name)
    private val qty: TextView = view.findViewById(R.id.qty)
    private val price: TextView = view.findViewById(R.id.price)
    private val image: ImageView = view.findViewById(R.id.image)
    private val tools= Tools()

    private var order: OrderItem? = null

    init {
        view.setOnClickListener {
            //view.context.startActivity(OrderActivity.createOrderIntent(view.context, order))
        }
    }

    fun bind(order: OrderItem?) {
        if (order == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
        } else {
            showRepoData(order)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showRepoData(order: OrderItem) {
        this.order = order
        name.text = order.menu_name
        qty.text = "qty: "+order.qty
        price.text = "price: "+order.total_cost + order.currency
        tools.displayImageBusiness(itemView.context, image, order.menu_image.toString())

    }

    companion object {
        fun create(parent: ViewGroup): OrderItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_order_item, parent, false)
            return OrderItemViewHolder(view)
        }
    }
}
