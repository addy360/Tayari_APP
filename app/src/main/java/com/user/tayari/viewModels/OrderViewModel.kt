package com.user.tayari.viewModels

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.*
import com.user.tayari.datasource.repository.OrderRepository
import com.user.tayari.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class OrderViewModel(orderRepository: OrderRepository) : ViewModel() {

    private val repository = orderRepository


    @ExperimentalPagingApi
    fun getOrders(context: Context, id: String): Flow<PagingData<OrderUiModel>> {

        val newResult: Flow<PagingData<OrderUiModel>> = repository.getOrders(context,id)
            .map { pagingData -> pagingData.map { OrderUiModel.OrderItem(it) } }
            .map {
                it.insertSeparators<OrderUiModel.OrderItem, OrderUiModel> { before, after ->

                    if (after == null) {
                        // we're at the end of the list
                        return@insertSeparators null
                    }

                    // check between 2 items
                    if (before?.status == after.status) {
                        if (after.status == "accepted") {
                            OrderUiModel.StatusItem ("PROCESSING")
                        } else {
                            OrderUiModel.StatusItem ("ORDERS")
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

sealed class OrderUiModel{
    data class OrderItem(val order: Order) : OrderUiModel()
    data class StatusItem(val status: String): OrderUiModel()
}

private val OrderUiModel.OrderItem.status: String
            get() = this.order.order_status.toString()

class OrderViewModelFactory(private val appRepository: OrderRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                orderRepository = appRepository

            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}