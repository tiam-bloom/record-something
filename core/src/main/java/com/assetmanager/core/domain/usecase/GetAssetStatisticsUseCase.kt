package com.assetmanager.core.domain.usecase

import com.assetmanager.core.data.repository.AssetRepository
import com.assetmanager.core.data.repository.CategoryRepository
import com.assetmanager.core.domain.model.AssetStatistics
import com.assetmanager.core.domain.model.CategoryStat
import com.assetmanager.core.domain.model.MonthlyStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAssetStatisticsUseCase @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val calculateDepreciationUseCase: CalculateDepreciationUseCase
) {
    fun getStatistics(): Flow<AssetStatistics> {
        return combine(
            assetRepository.getAllAssets(),
            assetRepository.getTotalAssetValue()
        ) { assets, totalValue ->
            val categoryMap = mutableMapOf<Long?, MutableList<Double>>()
            
            assets.forEach { asset ->
                val currentValue = calculateDepreciationUseCase.calculateCurrentValue(asset)
                categoryMap.getOrPut(asset.categoryId) { mutableListOf() }.add(currentValue)
            }

            val categoryStats = categoryMap.map { (categoryId, values) ->
                val totalCategoryValue = values.sum()
                CategoryStat(
                    categoryId = categoryId,
                    categoryName = assets.find { it.categoryId == categoryId }?.categoryName ?: "未分类",
                    categoryColor = assets.find { it.categoryId == categoryId }?.categoryColor ?: "#9E9E9E",
                    totalValue = totalCategoryValue,
                    assetCount = values.size,
                    percentage = if (totalValue > 0) totalCategoryValue / totalValue * 100 else 0.0
                )
            }.sortedByDescending { it.totalValue }

            AssetStatistics(
                totalValue = totalValue,
                totalPurchasePrice = assets.sumOf { it.purchasePrice },
                totalDepreciated = assets.sumOf { it.purchasePrice - totalValue },
                assetCount = assets.size,
                categoryStats = categoryStats
            )
        }
    }

    fun getMonthlyStats(year: Int): Flow<List<MonthlyStat>> {
        val startOfYear = LocalDate.of(year, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfYear = LocalDate.of(year, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return assetRepository.getAssetsByDateRange(startOfYear, endOfYear).combine(
            assetRepository.getAllAssets()
        ) { yearAssets, allAssets ->
            val monthlyMap = mutableMapOf<Int, MutableList<Double>>()
            
            yearAssets.forEach { asset ->
                val month = asset.purchaseDate.monthValue
                monthlyMap.getOrPut(month) { mutableListOf() }.add(asset.purchasePrice)
            }

            (1..12).map { month ->
                MonthlyStat(
                    year = year,
                    month = month,
                    totalPurchasePrice = monthlyMap[month]?.sum() ?: 0.0,
                    assetCount = monthlyMap[month]?.size ?: 0
                )
            }
        }
    }
}
