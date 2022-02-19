package com.example.testapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.luminosity.db.RemoteKeysDao
import com.example.luminosity.db.RepoRemoteKeys
import com.example.testapp.data.models.RemoteRepository

@Database(entities = [
    RemoteRepository::class,
    RepoRemoteKeys::class], version = 1)

abstract class GithubDatabase : RoomDatabase() {

    abstract fun githubRepoDao(): RepoDao
    abstract fun githubRemoteKeysDao(): RemoteKeysDao

    companion object Constants {
        const val DB_NAME = "GithubDatabase"
        const val GITHUB_TABLE = "github_repo_table"
        const val GITHUB_REMOTE_TABLE = "github_remote_keys_table"
    }

}