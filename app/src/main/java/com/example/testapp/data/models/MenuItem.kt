package com.example.testapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapp.db.FoodDatabase

@Entity(tableName = FoodDatabase.SPOONACULAR_TABLE)
data class MenuItem (
    @PrimaryKey
    val id: Long,
    val title: String,
    val image: String?,
    val restaurantChain: String?,
    )