package com.example.testapp.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapp.db.GithubDatabase
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = GithubDatabase.GITHUB_TABLE)
@JsonClass(generateAdapter = true)

data class RemoteRepository (
    @PrimaryKey
    val id: Long,
    @Embedded
    val owner: Owner,
    val name: String,
    val description: String?,
    @SerializedName("stargazers_count")
    @Json(name = "stargazers_count")
    val stars: Int,
    val language: String?,
    @SerializedName("watchers_count")
    @Json(name = "watchers_count")
    val watchers: Int,
    @SerializedName("forks_count")
    @Json(name = "forks_count")
    val forks: Int
    ): java.io.Serializable