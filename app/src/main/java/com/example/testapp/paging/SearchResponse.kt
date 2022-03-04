
package com.example.testapp.paging

import com.example.testapp.data.models.MenuItem
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("totalMenuItems") val total: Int = 0,
    @SerializedName("menuItems") val items: List<MenuItem> = emptyList(),
)
