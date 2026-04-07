package com.assetmanager.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val categoryId: Long?,
    val brand: String? = null,
    val model: String? = null,
    val purchaseDate: Long,
    val purchasePrice: Double,
    val currentValue: Double,
    val depreciationMethod: String = "STRAIGHT_LINE",
    val notes: String? = null,
    val imagePath: String? = null,
    val designLifeYears: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
