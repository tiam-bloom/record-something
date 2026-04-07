package com.assetmanager.core.data.repository

import com.assetmanager.core.data.dao.AssetDao
import com.assetmanager.core.data.dao.CategoryDao
import com.assetmanager.core.domain.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao
) {
    fun getAllAssets(): Flow<List<Asset>> {
        return combine(
            assetDao.getAllAssets(),
            categoryDao.getAllCategories()
        ) { assets, categories ->
            val categoryMap = categories.associateBy { it.id }
            assets.map { asset ->
                val category = asset.categoryId?.let { categoryMap[it] }
                asset.toDomain(category?.name, category?.color)
            }
        }
    }

    suspend fun getAssetById(id: Long): Asset? {
        val assetEntity = assetDao.getAssetById(id) ?: return null
        val category = assetEntity.categoryId?.let { categoryDao.getCategoryById(it) }
        return assetEntity.toDomain(category?.name, category?.color)
    }

    fun getAssetByIdFlow(id: Long): Flow<Asset?> {
        return combine(
            assetDao.getAssetByIdFlow(id),
            categoryDao.getAllCategories()
        ) { asset, categories ->
            val categoryMap = categories.associateBy { it.id }
            asset?.let {
                val category = it.categoryId?.let { catId -> categoryMap[catId] }
                it.toDomain(category?.name, category?.color)
            }
        }
    }

    fun getAssetsByCategory(categoryId: Long): Flow<List<Asset>> {
        return combine(
            assetDao.getAssetsByCategory(categoryId),
            categoryDao.getAllCategories()
        ) { assets, categories ->
            val categoryMap = categories.associateBy { it.id }
            assets.map { asset ->
                val category = asset.categoryId?.let { categoryMap[it] }
                asset.toDomain(category?.name, category?.color)
            }
        }
    }

    fun getRecentAssets(limit: Int = 5): Flow<List<Asset>> {
        return combine(
            assetDao.getRecentAssets(limit),
            categoryDao.getAllCategories()
        ) { assets, categories ->
            val categoryMap = categories.associateBy { it.id }
            assets.map { asset ->
                val category = asset.categoryId?.let { categoryMap[it] }
                asset.toDomain(category?.name, category?.color)
            }
        }
    }

    fun getAssetsByDateRange(startDate: Long, endDate: Long): Flow<List<Asset>> {
        return combine(
            assetDao.getAssetsByDateRange(startDate, endDate),
            categoryDao.getAllCategories()
        ) { assets, categories ->
            val categoryMap = categories.associateBy { it.id }
            assets.map { asset ->
                val category = asset.categoryId?.let { categoryMap[it] }
                asset.toDomain(category?.name, category?.color)
            }
        }
    }

    fun getTotalAssetValue(): Flow<Double> {
        return assetDao.getTotalAssetValue().map { it ?: 0.0 }
    }

    fun getTotalPurchasePriceByDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return assetDao.getTotalPurchasePriceByDateRange(startDate, endDate).map { it ?: 0.0 }
    }

    fun searchAssets(keyword: String): Flow<List<Asset>> {
        return combine(
            assetDao.searchAssets(keyword),
            categoryDao.getAllCategories()
        ) { assets, categories ->
            val categoryMap = categories.associateBy { it.id }
            assets.map { asset ->
                val category = asset.categoryId?.let { categoryMap[it] }
                asset.toDomain(category?.name, category?.color)
            }
        }
    }

    suspend fun insertAsset(asset: Asset): Long {
        return assetDao.insertAsset(asset.toEntity())
    }

    suspend fun updateAsset(asset: Asset) {
        assetDao.updateAsset(asset.toEntity())
    }

    suspend fun deleteAsset(asset: Asset) {
        assetDao.deleteAsset(asset.toEntity())
    }

    suspend fun deleteAssetById(id: Long) {
        assetDao.deleteAssetById(id)
    }

    suspend fun getAssetCount(): Int {
        return assetDao.getAssetCount()
    }

    suspend fun getAssetCountByDateRange(startDate: Long, endDate: Long): Int {
        return assetDao.getAssetCountByDateRange(startDate, endDate)
    }
}
