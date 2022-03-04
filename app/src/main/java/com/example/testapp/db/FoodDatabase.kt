/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.testapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.codelabs.paging.db.RemoteKeys
import com.example.android.codelabs.paging.db.RemoteKeysDao
import com.example.testapp.data.models.MenuItem


@Database(
    entities = [MenuItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object Constants {
        const val DB_NAME = "SpoonacularDatabase"
        const val SPOONACULAR_TABLE = "spoonacular_item_table"
        const val SPOONACULAR_REMOTE_TABLE = "spoonacular_remote_keys_table"
    }
//    companion object {
//
//        @Volatile
//        private var INSTANCE: FoodDatabase? = null
//
//        fun getInstance(context: Context): FoodDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE
//                    ?: buildDatabase(context).also { INSTANCE = it }
//            }
//
//        private fun buildDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                FoodDatabase::class.java, "Github.db"
//            )
//                .build()
//    }
}
