package com.example.testapp.data

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.testapp.Networking.GithubApi
import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.db.GithubDatabase
import com.example.testapp.paging.SearchRemoteMediator
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class Repository@Inject constructor(
    private val api: GithubApi,
    private val database: GithubDatabase,
    private val context: Application
) {
    fun getSearchResultStream(query: String): Flow<PagingData<RemoteRepository>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.githubRepoDao().reposByName(dbQuery) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = SearchRemoteMediator(
                query,
                api,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}