package com.example.luminosity.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM github_remote_keys_table WHERE id =:id")
    suspend fun getRemoteKeys(id: Long): RepoRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<RepoRemoteKeys>)

    @Query("DELETE FROM github_remote_keys_table")
    suspend fun deleteAllRemoteKeys()
}