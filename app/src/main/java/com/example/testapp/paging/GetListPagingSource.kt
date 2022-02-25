package com.example.testapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testapp.data.models.MenuItem
import retrofit2.HttpException
import java.io.IOException

class GetListPagingSource(
    private val queryKey: String,
    private val getData: suspend (query: String,page: Int) -> List<MenuItem>,
) : PagingSource<Int, MenuItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MenuItem> {
        val page = params.key ?: 1
        return try {
            val response = getData(queryKey, page)
            LoadResult.Page(
                response, prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MenuItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}