package com.user.tayari.results

import com.google.gson.annotations.SerializedName
import com.user.tayari.model.Restaurant

/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class RestaurantsResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Restaurant> = emptyList(),
        val nextPage: Int? = null
)
