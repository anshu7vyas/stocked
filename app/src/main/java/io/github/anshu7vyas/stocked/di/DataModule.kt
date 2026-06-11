package io.github.anshu7vyas.stocked.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.anshu7vyas.stocked.data.ProductDao
import io.github.anshu7vyas.stocked.data.StockedDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StockedDatabase =
        Room.databaseBuilder(context, StockedDatabase::class.java, StockedDatabase.NAME)
            .build()

    @Provides
    @Singleton
    fun provideProductDao(database: StockedDatabase): ProductDao = database.productDao()
}
