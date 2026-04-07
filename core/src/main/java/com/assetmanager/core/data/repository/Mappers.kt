package com.assetmanager.core.data.repository

import com.assetmanager.core.data.entity.AssetEntity
import com.assetmanager.core.data.entity.CategoryEntity
import com.assetmanager.core.data.entity.DepreciationRecordEntity
import com.assetmanager.core.domain.model.Asset
import com.assetmanager.core.domain.model.Category
import com.assetmanager.core.domain.model.DepreciationMethod
import com.assetmanager.core.domain.model.DepreciationRecord
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun AssetEntity.toDomain(categoryName: String? = null, categoryColor: String? = null): Asset {
    return Asset(
        id = id,
        name = name,
        categoryId = categoryId,
        categoryName = categoryName,
        categoryColor = categoryColor,
        brand = brand,
        model = model,
        purchaseDate = Instant.ofEpochMilli(purchaseDate).atZone(ZoneId.systemDefault()).toLocalDate(),
        purchasePrice = purchasePrice,
        currentValue = currentValue,
        depreciationMethod = DepreciationMethod.entries.find { it.name == depreciationMethod } ?: DepreciationMethod.STRAIGHT_LINE,
        notes = notes,
        imagePath = imagePath,
        designLifeYears = designLifeYears,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Asset.toEntity(): AssetEntity {
    return AssetEntity(
        id = id,
        name = name,
        categoryId = categoryId,
        brand = brand,
        model = model,
        purchaseDate = purchaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        purchasePrice = purchasePrice,
        currentValue = currentValue,
        depreciationMethod = depreciationMethod.name,
        notes = notes,
        imagePath = imagePath,
        designLifeYears = designLifeYears,
        createdAt = createdAt,
        updatedAt = System.currentTimeMillis()
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        defaultDepreciationRate = defaultDepreciationRate,
        parentId = parentId,
        sortOrder = sortOrder,
        createdAt = createdAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        defaultDepreciationRate = defaultDepreciationRate,
        parentId = parentId,
        sortOrder = sortOrder,
        createdAt = createdAt
    )
}

fun DepreciationRecordEntity.toDomain(): DepreciationRecord {
    return DepreciationRecord(
        id = id,
        assetId = assetId,
        recordDate = Instant.ofEpochMilli(recordDate).atZone(ZoneId.systemDefault()).toLocalDate(),
        value = value,
        dailyCost = dailyCost
    )
}

fun DepreciationRecord.toEntity(): DepreciationRecordEntity {
    return DepreciationRecordEntity(
        id = id,
        assetId = assetId,
        recordDate = recordDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        value = value,
        dailyCost = dailyCost
    )
}
