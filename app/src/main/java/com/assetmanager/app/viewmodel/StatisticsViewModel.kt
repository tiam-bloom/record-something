package com.assetmanager.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assetmanager.core.domain.model.AssetStatistics
import com.assetmanager.core.domain.model.CategoryStat
import com.assetmanager.core.domain.model.MonthlyStat
import com.assetmanager.core.domain.usecase.GetAssetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StatisticsUiState(
    val statistics: AssetStatistics = AssetStatistics(),
    val monthlyStats: List<MonthlyStat> = emptyList(),
    val selectedYear: Int = LocalDate.now().year,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAssetStatisticsUseCase: GetAssetStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
        loadMonthlyStats()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            getAssetStatisticsUseCase.getStatistics().collect { stats ->
                _uiState.update { it.copy(statistics = stats, isLoading = false) }
            }
        }
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            getAssetStatisticsUseCase.getMonthlyStats(_uiState.value.selectedYear).collect { monthlyStats ->
                _uiState.update { it.copy(monthlyStats = monthlyStats) }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun selectYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        loadMonthlyStats()
    }
}
