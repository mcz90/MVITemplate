package com.czyzewski.data.di

import androidx.room.Room
import com.czyzewski.data.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }
}

private const val DB_NAME = "DB_NAME"