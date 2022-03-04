package com.example.testapp.networking

import com.example.testapp.paging.SearchResponse
import retrofit2.http.*

interface SpoonacularApi {

    @GET("/food/menuItems/search")
    suspend fun searchItems(
        @Query("apiKey") key: String,
        @Query("query") query: String,
        @Query("offset") page: Int,
        @Query("number") itemsPerPage: Int
    ): SearchResponse
}

