package com.example.testapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.testapp.data.models.CategoryItem
import com.example.testapp.networking.SpoonacularApi
import com.example.testapp.data.models.MenuItem
import com.example.testapp.db.FoodDatabase
import com.example.testapp.paging.RemoteMediator
import kotlinx.coroutines.flow.Flow


import javax.inject.Inject

class Repository@Inject constructor(
    private val api: SpoonacularApi,
    private val dataBase: FoodDatabase
) {
    val categoryList = listOf(
        CategoryItem("Пицца", "Pizza",true),
        CategoryItem("Бургеры", "Burger"),
        CategoryItem("Закуски", "Appetizer"),
        CategoryItem("Роллы", "Sushi"),
        CategoryItem("Десерты", "Dessert"),
        CategoryItem("Напитки", "Drink")
    )

    fun getSearchResultStream(query: String): Flow<PagingData<MenuItem>> {
        val pagingSourceFactory = { dataBase.foodDao().itemsByName() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = RemoteMediator(
                query,
                api,
                dataBase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}