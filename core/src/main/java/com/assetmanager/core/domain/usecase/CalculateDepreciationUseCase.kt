package com.assetmanager.core.domain.usecase

import com.assetmanager.core.domain.model.Asset
import com.assetmanager.core.domain.model.DepreciationMethod
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculateDepreciationUseCase @Inject constructor() {

    fun calculateCurrentValue(asset: Asset, targetDate: LocalDate = LocalDate.now()): Double {
        val daysUsed = ChronoUnit.DAYS.between(asset.purchaseDate, targetDate)
        if (daysUsed <= 0) return asset.purchasePrice

        val totalDays = ChronoUnit.DAYS.between(asset.purchaseDate, targetDate)
        val yearsUsed = totalDays.toDouble() / 365.0

        val currentValue = when (asset.depreciationMethod) {
            DepreciationMethod.STRAIGHT_LINE -> calculateStraightLine(asset, yearsUsed)
            DepreciationMethod.DOUBLE_DECLINING -> calculateDoubleDeclining(asset, yearsUsed)
            DepreciationMethod.SUM_OF_YEARS_DIGITS -> calculateSumOfYearsDigits(asset, yearsUsed)
        }

        return maxOf(currentValue, 0.0)
    }

    private fun calculateStraightLine(asset: Asset, yearsUsed: Double): Double {
        val annualDepreciationRate = 0.2
        val totalDepreciation = yearsUsed * annualDepreciationRate
        val currentValueRatio = 1.0 - totalDepreciation
        return asset.purchasePrice * maxOf(currentValueRatio, 0.1)
    }

    private fun calculateDoubleDeclining(asset: Asset, yearsUsed: Double): Double {
        val rate = 0.4
        val currentValue = asset.purchasePrice * Math.pow(1.0 - rate, yearsUsed)
        return maxOf(currentValue, asset.purchasePrice * 0.1)
    }

    private fun calculateSumOfYearsDigits(asset: Asset, yearsUsed: Double): Double {
        val usefulLife = 5
        val sumOfYears = (1..usefulLife).sum()
        var totalDepreciation = 0.0

        for (year in 1..usefulLife.toLong()) {
            if (yearsUsed >= year) {
                totalDepreciation += (usefulLife - year + 1) * asset.purchasePrice / sumOfYears
            } else if (yearsUsed > year - 1) {
                val partialYear = yearsUsed - (year - 1)
                totalDepreciation += partialYear * (usefulLife - year + 1) * asset.purchasePrice / sumOfYears
                break
            }
        }

        return maxOf(asset.purchasePrice - totalDepreciation, asset.purchasePrice * 0.1)
    }

    fun calculateDailyCost(asset: Asset): Double {
        val daysUsed = ChronoUnit.DAYS.between(asset.purchaseDate, LocalDate.now())
        if (daysUsed <= 0) return 0.0
        val currentValue = calculateCurrentValue(asset)
        return (asset.purchasePrice - currentValue) / daysUsed
    }

    fun calculateMonthlyCost(asset: Asset): Double {
        return calculateDailyCost(asset) * 30
    }

    fun calculateYearlyCost(asset: Asset): Double {
        return calculateDailyCost(asset) * 365
    }

    fun calculateRemainingValue(asset: Asset): Double {
        return calculateCurrentValue(asset)
    }

    fun calculateEstimatedRemainingDays(asset: Asset): Long? {
        val designLifeYears = asset.designLifeYears ?: return null
        val totalDays = designLifeYears * 365L
        val usedDays = ChronoUnit.DAYS.between(asset.purchaseDate, LocalDate.now())
        return maxOf(0, totalDays - usedDays)
    }
}
