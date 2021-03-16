package com.lockminds.tayari.results

import com.google.gson.annotations.SerializedName
import com.lockminds.tayari.model.Restaurant

/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class RestaurantsResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Restaurant> = emptyList(),
        val nextPage: Int? = null
)
