package com.example.testapp.data.models

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
@JsonClass(generateAdapter = true)
data class Owner (
    val login: String,
    val avatar_url: String
)
