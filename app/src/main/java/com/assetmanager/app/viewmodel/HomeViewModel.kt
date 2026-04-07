package com.assetmanager.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assetmanager.core.data.repository.AssetRepository
import com.assetmanager.core.domain.model.Asset
import com.assetmanager.core.domain.usecase.CalculateDepreciationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val totalValue: Double = 0.0,
    val monthlyAddedValue: Double = 0.0,
    val monthlyAddedCount: Int = 0,
    val recentAssets: List<Asset> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val calculateDepreciationUseCase: CalculateDepreciationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            combine(
                assetRepository.getTotalAssetValue(),
                assetRepository.getTotalPurchasePriceByDateRange(startOfMonth, endOfMonth),
                assetRepository.getRecentAssets(5)
            ) { totalValue, monthlyValue, recentAssets ->
                HomeUiState(
                    totalValue = totalValue,
                    monthlyAddedValue = monthlyValue,
                    monthlyAddedCount = recentAssets.size,
                    recentAssets = recentAssets.map { asset ->
                        asset.copy(
                            currentValue = calculateDepreciationUseCase.calculateCurrentValue(asset)
                        )
                    },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
