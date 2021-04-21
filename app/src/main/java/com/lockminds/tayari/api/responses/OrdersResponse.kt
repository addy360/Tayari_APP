package com.lockminds.tayari.api.responses

import com.google.gson.annotations.SerializedName
import com.lockminds.tayari.model.Order

/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class OrdersResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Order> = emptyList(),
        val nextPage: Int? = null
)
