package com.assetmanager.core.domain.model

data class AssetStatistics(
    val totalValue: Double = 0.0,
    val totalPurchasePrice: Double = 0.0,
    val totalDepreciated: Double = 0.0,
    val assetCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val monthlyStats: List<MonthlyStat> = emptyList()
)

data class CategoryStat(
    val categoryId: Long?,
    val categoryName: String,
    val categoryColor: String,
    val totalValue: Double,
    val assetCount: Int,
    val percentage: Double
)

data class MonthlyStat(
    val year: Int,
    val month: Int,
    val totalPurchasePrice: Double,
    val assetCount: Int
)
