package com.assetmanager.core.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val defaultDepreciationRate: Double = 0.1,
    val parentId: Long? = null,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
