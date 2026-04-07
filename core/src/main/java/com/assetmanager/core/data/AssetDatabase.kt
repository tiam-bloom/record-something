package com.assetmanager.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.assetmanager.core.data.dao.AssetDao
import com.assetmanager.core.data.dao.CategoryDao
import com.assetmanager.core.data.dao.DepreciationRecordDao
import com.assetmanager.core.data.entity.AssetEntity
import com.assetmanager.core.data.entity.CategoryEntity
import com.assetmanager.core.data.entity.DepreciationRecordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AssetEntity::class,
        CategoryEntity::class,
        DepreciationRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AssetDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun depreciationRecordDao(): DepreciationRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AssetDatabase? = null

        fun getDatabase(context: Context): AssetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AssetDatabase::class.java,
                    "asset_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.categoryDao())
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                CategoryEntity(name = "手机平板", icon = "phone_android", color = "#4CAF50", defaultDepreciationRate = 0.2, sortOrder = 1),
                CategoryEntity(name = "电脑设备", icon = "computer", color = "#2196F3", defaultDepreciationRate = 0.15, sortOrder = 2),
                CategoryEntity(name = "家用电器", icon = "home", color = "#FF9800", defaultDepreciationRate = 0.1, sortOrder = 3),
                CategoryEntity(name = "摄影器材", icon = "camera_alt", color = "#9C27B0", defaultDepreciationRate = 0.15, sortOrder = 4),
                CategoryEntity(name = "游戏设备", icon = "sports_esports", color = "#E91E63", defaultDepreciationRate = 0.2, sortOrder = 5),
                CategoryEntity(name = "家具家居", icon = "chair", color = "#795548", defaultDepreciationRate = 0.08, sortOrder = 6),
                CategoryEntity(name = "办公设备", icon = "print", color = "#607D8B", defaultDepreciationRate = 0.1, sortOrder = 7),
                CategoryEntity(name = "车辆出行", icon = "directions_car", color = "#F44336", defaultDepreciationRate = 0.15, sortOrder = 8),
                CategoryEntity(name = "首饰收藏", icon = "diamond", color = "#FFD700", defaultDepreciationRate = 0.05, sortOrder = 9),
                CategoryEntity(name = "其他资产", icon = "category", color = "#9E9E9E", defaultDepreciationRate = 0.1, sortOrder = 10)
            )
            categoryDao.insertCategories(defaultCategories)
        }
    }
}
