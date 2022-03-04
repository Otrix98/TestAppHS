package com.example.testapp.di

import android.app.Application
import androidx.room.Room
import com.example.testapp.db.FoodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

@Provides
@Singleton
fun providesDatabase(application: Application): FoodDatabase {
    return Room.databaseBuilder(
        application,
        FoodDatabase::class.java,
        FoodDatabase.DB_NAME
    )
        .build()
}
}