package com.assetmanager.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assetmanager.core.data.repository.CategoryRepository
import com.assetmanager.core.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, editingCategory = null) }
    }

    fun showEditDialog(category: Category) {
        _uiState.update { it.copy(showAddDialog = true, editingCategory = category) }
    }

    fun hideDialog() {
        _uiState.update { it.copy(showAddDialog = false, editingCategory = null) }
    }

    fun saveCategory(name: String, icon: String, color: String, depreciationRate: Double) {
        viewModelScope.launch {
            val editing = _uiState.value.editingCategory
            if (editing != null) {
                categoryRepository.updateCategory(
                    editing.copy(
                        name = name,
                        icon = icon,
                        color = color,
                        defaultDepreciationRate = depreciationRate
                    )
                )
            } else {
                categoryRepository.insertCategory(
                    Category(
                        name = name,
                        icon = icon,
                        color = color,
                        defaultDepreciationRate = depreciationRate
                    )
                )
            }
            hideDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
