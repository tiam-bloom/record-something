package com.assetmanager.core.domain.model

import java.time.LocalDate

data class DepreciationRecord(
    val id: Long = 0,
    val assetId: Long,
    val recordDate: LocalDate,
    val value: Double,
    val dailyCost: Double
)
