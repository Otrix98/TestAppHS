package com.example.testapp.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.luminosity.db.RepoRemoteKeys
import com.example.testapp.Networking.GithubApi
import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.db.GithubDatabase
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val api: GithubApi,
    private val database: GithubDatabase
) : RemoteMediator<Int, RemoteRepository>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, RemoteRepository>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPage?.minus(1) ?: UNSPLASH_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevPage
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPage
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val apiResponse = api.searchRepos(query, page, state.config.pageSize)

            val repos = apiResponse.items
            val endOfPaginationReached = repos.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.githubRemoteKeysDao().deleteAllRemoteKeys()
                    database.githubRepoDao().deleteAllImages()
                }
                val prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = repos.map {
                    RepoRemoteKeys(id = it.id, prevPage = prevKey, nextPage = nextKey)
                }
                database.githubRemoteKeysDao().addAllRemoteKeys(keys)
                database.githubRepoDao().addImages(repos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, RemoteRepository>
    ): RepoRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                database.githubRemoteKeysDao().getRemoteKeys(id = repo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, RemoteRepository>
    ): RepoRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                database.githubRemoteKeysDao().getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, RemoteRepository>
    ): RepoRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.githubRemoteKeysDao().getRemoteKeys(id = id)
            }
        }
    }
}