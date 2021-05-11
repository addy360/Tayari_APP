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

package com.lockminds.tayari.viewHolders

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lockminds.tayari.R
import com.lockminds.tayari.RestaurantActivity
import com.lockminds.tayari.Tools
import com.lockminds.tayari.model.Restaurant

/**
 * View Holder for a [Restaurant] RecyclerView list item.
 */
class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.name)
    private val address: TextView = view.findViewById(R.id.address)
    private val image: ImageView = view.findViewById(R.id.logo)

    private var repo: Restaurant? = null
    private var tools = Tools()

    init {
        view.setOnClickListener {
            view.context.startActivity(RestaurantActivity.createRestaurantIntent(view.context, repo))
        }
    }

    fun bind(repo: Restaurant?) {
        if (repo == null) {
            val resources = itemView.resources
            name.text = resources.getString(R.string.loading)
        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(repo: Restaurant) {
        this.repo = repo
        name.text = repo.name
        address.text = repo.address
        tools.displayImageBusiness(image.context,image,repo.logo.toString())
    }

    companion object {
        fun create(parent: ViewGroup): RepoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_view_item, parent, false)
            return RepoViewHolder(view)
        }
    }
}
