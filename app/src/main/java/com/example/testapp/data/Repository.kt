package com.example.testapp.data

import com.example.testapp.networking.SpoonacularApi
import com.example.testapp.data.models.MenuItem


import javax.inject.Inject

class Repository@Inject constructor(
    private val api: SpoonacularApi,
) {
    suspend fun getItems(query: String, page: Int): List<MenuItem> {
       return api.searchItems(API_KEY_3, categoryTranslator(query), page, NETWORK_PAGE_SIZE).menuItems
    }

    private fun categoryTranslator(string: String): String {
        return when (string) {
            "Пицца" -> "pizza"
            "Бургеры" -> "burger"
            "Закуски" -> "Snack"
            "Роллы" -> "sushi"
            "Десерты" -> "dessert"
            "Напитки" -> "drink"
            else -> {
                "kebab"
            }
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
        const val API_KEY = "92338f76d94f4fdeb0f9f7b183182e3a"
        const val API_KEY_2 = "7b80454330694ba39cbd5a0ab006b146"
        const val API_KEY_3 = "3a88a09ef93342298911c52465535ae6"
    }
}