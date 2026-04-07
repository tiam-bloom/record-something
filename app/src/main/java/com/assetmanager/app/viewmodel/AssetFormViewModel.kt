package com.assetmanager.app.viewmodel

import androidx.lifecycle.SavedStateHandle
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

data class AssetFormUiState(
    val name: String = "",
    val categoryId: Long? = null,
    val brand: String = "",
    val model: String = "",
    val purchaseDate: LocalDate = LocalDate.now(),
    val purchasePrice: String = "",
    val depreciationMethod: DepreciationMethod = DepreciationMethod.STRAIGHT_LINE,
    val notes: String = "",
    val designLifeYears: String = "",
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AssetFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assetRepository: AssetRepository,
    private val categoryRepository: CategoryRepository,
    private val calculateDepreciationUseCase: CalculateDepreciationUseCase
) : ViewModel() {

    private val assetId: Long? = savedStateHandle.get<Long>("assetId")?.takeIf { it > 0 }
    val isEditMode: Boolean = assetId != null

    private val _uiState = MutableStateFlow(AssetFormUiState(isEditMode = isEditMode))
    val uiState: StateFlow<AssetFormUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        if (isEditMode && assetId != null) {
            loadAsset(assetId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }
        }
    }

    private fun loadAsset(id: Long) {
        viewModelScope.launch {
            val asset = assetRepository.getAssetById(id)
            if (asset != null) {
                _uiState.update {
                    it.copy(
                        name = asset.name,
                        categoryId = asset.categoryId,
                        brand = asset.brand ?: "",
                        model = asset.model ?: "",
                        purchaseDate = asset.purchaseDate,
                        purchasePrice = asset.purchasePrice.toString(),
                        depreciationMethod = asset.depreciationMethod,
                        notes = asset.notes ?: "",
                        designLifeYears = asset.designLifeYears?.toString() ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun updateCategoryId(categoryId: Long?) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun updateBrand(brand: String) {
        _uiState.update { it.copy(brand = brand) }
    }

    fun updateModel(model: String) {
        _uiState.update { it.copy(model = model) }
    }

    fun updatePurchaseDate(date: LocalDate) {
        _uiState.update { it.copy(purchaseDate = date) }
    }

    fun updatePurchasePrice(price: String) {
        _uiState.update { it.copy(purchasePrice = price, errorMessage = null) }
    }

    fun updateDepreciationMethod(method: DepreciationMethod) {
        _uiState.update { it.copy(depreciationMethod = method) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun updateDesignLifeYears(years: String) {
        _uiState.update { it.copy(designLifeYears = years) }
    }

    fun saveAsset() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请输入资产名称") }
            return
        }

        val price = state.purchasePrice.toDoubleOrNull()
        if (price == null || price <= 0) {
            _uiState.update { it.copy(errorMessage = "请输入有效的购买价格") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val currentValue = if (isEditMode && assetId != null) {
                    val existingAsset = assetRepository.getAssetById(assetId)
                    existingAsset?.let {
                        calculateDepreciationUseCase.calculateCurrentValue(
                            it.copy(purchaseDate = state.purchaseDate, purchasePrice = price)
                        )
                    } ?: price
                } else {
                    price
                }

                val asset = Asset(
                    id = assetId ?: 0,
                    name = state.name,
                    categoryId = state.categoryId,
                    brand = state.brand.takeIf { it.isNotBlank() },
                    model = state.model.takeIf { it.isNotBlank() },
                    purchaseDate = state.purchaseDate,
                    purchasePrice = price,
                    currentValue = currentValue,
                    depreciationMethod = state.depreciationMethod,
                    notes = state.notes.takeIf { it.isNotBlank() },
                    designLifeYears = state.designLifeYears.toIntOrNull()
                )

                if (isEditMode) {
                    assetRepository.updateAsset(asset)
                } else {
                    assetRepository.insertAsset(asset)
                }

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "保存失败: ${e.message}") }
            }
        }
    }
}
