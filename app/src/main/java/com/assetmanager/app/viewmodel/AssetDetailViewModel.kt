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

data class AssetDetailUiState(
    val asset: Asset? = null,
    val currentValue: Double = 0.0,
    val dailyCost: Double = 0.0,
    val monthlyCost: Double = 0.0,
    val yearlyCost: Double = 0.0,
    val depreciatedAmount: Double = 0.0,
    val depreciationRate: Double = 0.0,
    val estimatedRemainingDays: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class AssetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val assetRepository: AssetRepository,
    private val calculateDepreciationUseCase: CalculateDepreciationUseCase
) : ViewModel() {

    private val assetId: Long = savedStateHandle.get<Long>("assetId") ?: 0L

    private val _uiState = MutableStateFlow(AssetDetailUiState())
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    init {
        loadAsset()
    }

    private fun loadAsset() {
        viewModelScope.launch {
            assetRepository.getAssetByIdFlow(assetId).collect { asset ->
                if (asset != null) {
                    val currentValue = calculateDepreciationUseCase.calculateCurrentValue(asset)
                    val dailyCost = calculateDepreciationUseCase.calculateDailyCost(asset)
                    val monthlyCost = calculateDepreciationUseCase.calculateMonthlyCost(asset)
                    val yearlyCost = calculateDepreciationUseCase.calculateYearlyCost(asset)
                    val estimatedRemainingDays = calculateDepreciationUseCase.calculateEstimatedRemainingDays(asset)

                    _uiState.value = AssetDetailUiState(
                        asset = asset,
                        currentValue = currentValue,
                        dailyCost = dailyCost,
                        monthlyCost = monthlyCost,
                        yearlyCost = yearlyCost,
                        depreciatedAmount = asset.purchasePrice - currentValue,
                        depreciationRate = if (asset.purchasePrice > 0) (asset.purchasePrice - currentValue) / asset.purchasePrice else 0.0,
                        estimatedRemainingDays = estimatedRemainingDays,
                        isLoading = false
                    )
                } else {
                    _uiState.value = AssetDetailUiState(isLoading = false)
                }
            }
        }
    }

    fun deleteAsset() {
        viewModelScope.launch {
            _uiState.value.asset?.let { asset ->
                assetRepository.deleteAsset(asset)
            }
        }
    }
}
