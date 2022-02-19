package com.example.testapp.Networking

import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.data.models.RepoSearchResponse
import retrofit2.Call
import retrofit2.http.*

interface GithubApi {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RepoSearchResponse
}