package com.lockminds.tayari.viewModels

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.lockminds.tayari.datasource.repository.OrderRepository
import com.lockminds.tayari.model.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderItemViewModel(orderRepository: OrderRepository) : ViewModel() {

    private val repository = orderRepository

    @ExperimentalPagingApi
    fun getOrderItems(context: Context, id: String): Flow<PagingData<OrderItemUiModel>> {

        val newResult: Flow<PagingData<OrderItemUiModel>> = repository.getOrderItems(context,id)
            .map { pagingData -> pagingData.map { OrderItemUiModel.OrderDataItem(it) } }
            .map {
                it.insertSeparators<OrderItemUiModel.OrderDataItem, OrderItemUiModel> { before, after ->

                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    // check between 2 items
                    if (before?.menu_name == after.menu_name) {
                        if (after.menu_name == "accepted") {
                            OrderItemUiModel.StatusItem ("PROCESSING")
                        } else {
                            OrderItemUiModel.StatusItem ("ORDERS")
                        }
                    } else {
                        // no separator
                        null
                    }

                }
            }
            .cachedIn(viewModelScope)
        return newResult
    }

}

sealed class OrderItemUiModel{
    data class OrderDataItem(val order: OrderItem) : OrderItemUiModel()
    data class StatusItem(val status: String): OrderItemUiModel()
}

private val OrderItemUiModel.OrderDataItem.menu_name: String
            get() = this.order.menu_name.toString()

class OrderItemViewModelFactory(private val appRepository: OrderRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderItemViewModel(
                orderRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}