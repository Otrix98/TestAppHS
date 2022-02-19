package com.example.testapp.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testapp.data.models.RemoteRepository


@Dao
interface RepoDao {

    @Query("SELECT * FROM github_repo_table")
    fun getAllImages(): PagingSource<Int, RemoteRepository>

    @Query(
        "SELECT * FROM github_repo_table WHERE " +
                "name LIKE :queryString OR description LIKE :queryString " +
                "ORDER BY stars DESC, name ASC"
    )
    fun reposByName(queryString: String): PagingSource<Int, RemoteRepository>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImages(images: List<RemoteRepository>)

    @Query("DELETE FROM github_repo_table ")
    suspend fun deleteAllImages()
}