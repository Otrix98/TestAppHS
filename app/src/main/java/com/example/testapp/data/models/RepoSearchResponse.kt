package com.example.testapp.data.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable


@Serializable
@JsonClass(generateAdapter = true)
data class RepoSearchResponse(
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<RemoteRepository> = emptyList(),
    val nextPage: Int? = null
)