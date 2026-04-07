package com.assetmanager.core.domain.model

import java.time.LocalDate

data class Asset(
    val id: Long = 0,
    val name: String,
    val categoryId: Long?,
    val categoryName: String? = null,
    val categoryColor: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val purchaseDate: LocalDate,
    val purchasePrice: Double,
    val currentValue: Double,
    val depreciationMethod: DepreciationMethod = DepreciationMethod.STRAIGHT_LINE,
    val notes: String? = null,
    val imagePath: String? = null,
    val designLifeYears: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val daysUsed: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(purchaseDate, LocalDate.now())

    val depreciatedAmount: Double
        get() = purchasePrice - currentValue

    val depreciationRate: Double
        get() = if (purchasePrice > 0) depreciatedAmount / purchasePrice else 0.0

    val dailyCost: Double
        get() = if (daysUsed > 0) depreciatedAmount / daysUsed else 0.0

    val monthlyCost: Double
        get() = dailyCost * 30

    val yearlyCost: Double
        get() = dailyCost * 365
}

enum class DepreciationMethod(val displayName: String) {
    STRAIGHT_LINE("直线折旧法"),
    DOUBLE_DECLINING("双倍余额递减法"),
    SUM_OF_YEARS_DIGITS("年数总和法")
}
