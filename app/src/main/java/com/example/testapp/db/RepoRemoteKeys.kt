package com.example.luminosity.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapp.db.GithubDatabase

@Entity(tableName = GithubDatabase.GITHUB_REMOTE_TABLE)
data class RepoRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val prevPage: Int?,
    val nextPage: Int?
)
