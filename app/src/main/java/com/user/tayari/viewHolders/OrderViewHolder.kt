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

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.user.tayari.R
import com.user.tayari.Tools
import com.user.tayari.model.Order
import com.user.tayari.ui.OrderActivity
import com.user.tayari.utils.Timer
import java.util.*

/**
 * View Holder for a [Order] RecyclerView list item.
 */
class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val date: TextView = view.findViewById(R.id.datetime)
    private val restaurant: TextView = view.findViewById(R.id.restaurant)
    private val price: TextView = view.findViewById(R.id.price)
    private val table: TextView = view.findViewById(R.id.table)
    private val image: ImageView = view.findViewById(R.id.image)
    private val elapseTimeHolder: View = view.findViewById(R.id.items_holder)
    private val elapseTime: TextView = view.findViewById(R.id.elapsed_time)
    private val tools = Tools()

    private var order: Order? = null

    init {
        view.setOnClickListener {
            view.context.startActivity(OrderActivity.createOrderIntent(view.context, order))
        }
    }

    fun bind(order: Order?) {
        if (order == null) {
            val resources = itemView.resources
            restaurant.text = resources.getString(R.string.loading)
        } else {
            showRepoData(order)
        }
    }


    private fun showRepoData(order: Order) {
        this.order = order
        val resources = itemView.resources
        if(!order.execution_time.isNullOrBlank() && order.order_status.equals("processing")){
            elapseTimeHolder.isVisible = true
            elapseTime.text = order.elapsed_time
        }

        if(order.balance.toString().toFloat() > 0 && order.order_status.equals("complete")){
            elapseTimeHolder.isVisible = true
            elapseTime.text = "Pay Now "+order.balance+order.currency
        }


        date.text = order.created_at
        table.text = "#"+order.id
        restaurant.text = order.restaurant_name
        price.text = order.order_status
        tools.displayImageBusiness(itemView.context, image, order.restaurant_logo.toString())
        if(order.order_status.equals("pending")){
            price.setTextColor(resources.getColor(R.color.pending))
        }
        if(order.order_status.equals("accepted")){
            price.setTextColor(resources.getColor(R.color.accepted))
        }

        if(order.order_status.equals("processing")){
            price.setTextColor(resources.getColor(R.color.process))
        }
        if(order.order_status.equals("complete")){
            price.setTextColor(resources.getColor(R.color.complete))
        }

    }

    companion object {
        fun create(parent: ViewGroup): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(view)
        }
    }
}
