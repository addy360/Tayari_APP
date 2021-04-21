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

package com.lockminds.tayari

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.lockminds.tayari.api.GithubService
import com.lockminds.tayari.api.services.OrderService
import com.lockminds.tayari.data.GithubRepository
import com.lockminds.tayari.datasource.AppDatabase
import com.lockminds.tayari.datasource.repository.OrderRepository
import com.lockminds.tayari.viewModels.OrderItemViewModelFactory
import com.lockminds.tayari.viewModels.OrderViewModelFactory
import com.lockminds.tayari.viewModels.ViewModelFactory

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injection {

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService] and a
     * [GithubLocalCache]
     */
    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), AppDatabase.getInstance(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }

    /**
     * Creates an instance of [OrderRepository] based on the [OrderService] and a
     * [OrderLocalCache]
     */
    private fun provideOrderRepository(context: Context): OrderRepository {
        return OrderRepository(OrderService.create(), AppDatabase.getInstance(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun orderViewModelFactory(context: Context): ViewModelProvider.Factory {
        return OrderViewModelFactory(provideOrderRepository(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     */
    fun orderItemsViewModelFactory(context: Context): ViewModelProvider.Factory {
        return OrderItemViewModelFactory(provideOrderRepository(context))
    }

}
