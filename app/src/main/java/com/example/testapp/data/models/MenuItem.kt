package com.example.testapp.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem (
    val id: Long,
    val title: String,
    val image: String?,
    val restaurantChain: String?,
    )