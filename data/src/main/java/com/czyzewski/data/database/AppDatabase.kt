package com.czyzewski.data.database

import androidx.room.*

@Database(
    entities = [TempEntity::class], version = 1
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase()

@Entity
data class TempEntity(@PrimaryKey val asd: String)




