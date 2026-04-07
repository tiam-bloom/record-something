package com.assetmanager.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assetmanager.core.data.repository.AssetRepository
import com.assetmanager.core.data.repository.CategoryRepository
import com.assetmanager.core.domain.model.Asset
import com.assetmanager.core.domain.model.Category
import com.assetmanager.core.domain.model.DepreciationMethod
import com.assetmanager.core.domain.usecase.CalculateDepreciationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AssetListUiState(
    val assets: List<Asset> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val viewMode: ViewMode = ViewMode.LIST
)

enum class ViewMode { LIST, GRID }

@HiltViewModel
class AssetListViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val calculateDepreciationUseCase: CalculateDepreciationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetListUiState())
    val uiState: StateFlow<AssetListUiState> = _uiState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _viewMode = MutableStateFlow(ViewMode.LIST)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                assetRepository.getAllAssets(),
                categoryRepository.getAllCategories(),
                _selectedCategoryId,
                _searchQuery,
                _viewMode
            ) { assets, categories, selectedCategory, searchQuery, viewMode ->
                val filteredAssets = assets
                    .filter { asset ->
                        (selectedCategory == null || asset.categoryId == selectedCategory) &&
                        (searchQuery.isEmpty() || asset.name.contains(searchQuery, ignoreCase = true) ||
                                asset.brand?.contains(searchQuery, ignoreCase = true) == true ||
                                asset.model?.contains(searchQuery, ignoreCase = true) == true)
                    }
                    .map { asset ->
                        asset.copy(
                            currentValue = calculateDepreciationUseCase.calculateCurrentValue(asset)
                        )
                    }

                AssetListUiState(
                    assets = filteredAssets,
                    categories = categories,
                    selectedCategoryId = selectedCategory,
                    searchQuery = searchQuery,
                    isLoading = false,
                    viewMode = viewMode
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleViewMode() {
        _viewMode.value = when (_viewMode.value) {
            ViewMode.LIST -> ViewMode.GRID
            ViewMode.GRID -> ViewMode.LIST
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch {
            assetRepository.deleteAsset(asset)
        }
    }
}
