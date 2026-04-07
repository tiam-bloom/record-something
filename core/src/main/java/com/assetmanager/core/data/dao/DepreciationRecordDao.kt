package com.assetmanager.core.data.dao

import androidx.room.*
import com.assetmanager.core.data.entity.DepreciationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepreciationRecordDao {
    @Query("SELECT * FROM depreciation_records WHERE assetId = :assetId ORDER BY recordDate ASC")
    fun getRecordsByAssetId(assetId: Long): Flow<List<DepreciationRecordEntity>>

    @Query("SELECT * FROM depreciation_records WHERE assetId = :assetId AND recordDate >= :startDate AND recordDate <= :endDate ORDER BY recordDate ASC")
    fun getRecordsByAssetIdAndDateRange(assetId: Long, startDate: Long, endDate: Long): Flow<List<DepreciationRecordEntity>>

    @Query("SELECT * FROM depreciation_records WHERE recordDate >= :startDate AND recordDate <= :endDate ORDER BY recordDate ASC")
    fun getRecordsByDateRange(startDate: Long, endDate: Long): Flow<List<DepreciationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DepreciationRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<DepreciationRecordEntity>)

    @Query("DELETE FROM depreciation_records WHERE assetId = :assetId")
    suspend fun deleteRecordsByAssetId(assetId: Long)

    @Query("DELETE FROM depreciation_records WHERE recordDate < :date")
    suspend fun deleteOldRecords(date: Long)
}
