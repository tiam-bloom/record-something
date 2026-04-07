package com.assetmanager.core.di

import android.content.Context
import com.assetmanager.core.data.AssetDatabase
import com.assetmanager.core.data.dao.AssetDao
import com.assetmanager.core.data.dao.CategoryDao
import com.assetmanager.core.data.dao.DepreciationRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AssetDatabase {
        return AssetDatabase.getDatabase(context)
    }

    @Provides
    fun provideAssetDao(database: AssetDatabase): AssetDao {
        return database.assetDao()
    }

    @Provides
    fun provideCategoryDao(database: AssetDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideDepreciationRecordDao(database: AssetDatabase): DepreciationRecordDao {
        return database.depreciationRecordDao()
    }
}
