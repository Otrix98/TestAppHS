package com.example.testapp.networking

import com.example.testapp.data.models.MenuItem
import com.squareup.moshi.JsonClass
import retrofit2.http.*

interface SpoonacularApi {

    @GET("/food/menuItems/search")
    suspend fun searchItems(
        @Query("apiKey") key: String,
        @Query("query") query: String,
        @Query("offset") page: Int,
        @Query("number") itemsPerPage: Int
    ): ResponseSearch
}

@JsonClass(generateAdapter = true)
class ResponseSearch(
    val menuItems: List<MenuItem>
)