package io.github.anshu7vyas.stocked.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = true)
abstract class StockedDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        const val NAME = "stocked.db"
    }
}
