package com.assetmanager.core.data.repository

import com.assetmanager.core.data.dao.CategoryDao
import com.assetmanager.core.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    fun getRootCategories(): Flow<List<Category>> {
        return categoryDao.getRootCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getChildCategories(parentId: Long): Flow<List<Category>> {
        return categoryDao.getChildCategories(parentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    suspend fun deleteCategoryById(id: Long) {
        categoryDao.deleteCategoryById(id)
    }

    suspend fun getCategoryCount(): Int {
        return categoryDao.getCategoryCount()
    }
}
