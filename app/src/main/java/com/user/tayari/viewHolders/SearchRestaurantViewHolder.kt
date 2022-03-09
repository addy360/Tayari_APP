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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.user.tayari.R
import com.user.tayari.RestaurantActivity
import com.user.tayari.Tools
import com.user.tayari.model.Restaurant
import com.user.tayari.model.RestaurantSearch

/**
 * View Holder for a [Restaurant] RecyclerView list item.
 */
class SearchRestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.name)
    private val address: TextView = view.findViewById(R.id.address)
    private val image: ImageView = view.findViewById(R.id.logo)
    private val tools = Tools()
    private var repo: RestaurantSearch? = null

    init {
        view.setOnClickListener {
            val rep = Restaurant()
            view.context.startActivity(RestaurantActivity.createRestaurantIntent(view.context, rep))
        }
    }

    fun bind(repo: RestaurantSearch?) {
        if (repo == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(repo: RestaurantSearch) {
        this.repo = repo
        name.text = repo.name
        address.text = repo.address
        tools.displayImageBusiness(image.context,image,repo.logo.toString())
    }

    companion object {
        fun create(parent: ViewGroup): SearchRestaurantViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_view_item, parent, false)
            return SearchRestaurantViewHolder(view)
        }
    }
}
