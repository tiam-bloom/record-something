package com.assetmanager.core.data.dao

import androidx.room.*
import com.assetmanager.core.data.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY createdAt DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Long): AssetEntity?

    @Query("SELECT * FROM assets WHERE id = :id")
    fun getAssetByIdFlow(id: Long): Flow<AssetEntity?>

    @Query("SELECT * FROM assets WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getAssetsByCategory(categoryId: Long): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentAssets(limit: Int): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE purchaseDate >= :startDate AND purchaseDate <= :endDate ORDER BY createdAt DESC")
    fun getAssetsByDateRange(startDate: Long, endDate: Long): Flow<List<AssetEntity>>

    @Query("SELECT SUM(currentValue) FROM assets")
    fun getTotalAssetValue(): Flow<Double?>

    @Query("SELECT SUM(purchasePrice) FROM assets WHERE purchaseDate >= :startDate AND purchaseDate <= :endDate")
    fun getTotalPurchasePriceByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT * FROM assets WHERE name LIKE '%' || :keyword || '%' OR brand LIKE '%' || :keyword || '%' OR model LIKE '%' || :keyword || '%'")
    fun searchAssets(keyword: String): Flow<List<AssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Update
    suspend fun updateAsset(asset: AssetEntity)

    @Delete
    suspend fun deleteAsset(asset: AssetEntity)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: Long)

    @Query("SELECT COUNT(*) FROM assets")
    suspend fun getAssetCount(): Int

    @Query("SELECT COUNT(*) FROM assets WHERE purchaseDate >= :startDate AND purchaseDate <= :endDate")
    suspend fun getAssetCountByDateRange(startDate: Long, endDate: Long): Int
}
