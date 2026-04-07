package com.assetmanager.app.ui.pages.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.assetmanager.app.ui.components.*
import com.assetmanager.app.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "统计分析",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("资产构成") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("价值趋势") }
                )
                Tab(
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    text = { Text("消费分析") }
                )
            }

            if (uiState.isLoading) {
                LoadingIndicator()
            } else {
                when (uiState.selectedTab) {
                    0 -> AssetCompositionTab(
                        statistics = uiState.statistics,
                        currencyFormat = currencyFormat
                    )
                    1 -> ValueTrendTab(
                        statistics = uiState.statistics,
                        currencyFormat = currencyFormat
                    )
                    2 -> ConsumptionAnalysisTab(
                        statistics = uiState.statistics,
                        monthlyStats = uiState.monthlyStats,
                        selectedYear = uiState.selectedYear,
                        onYearChange = { viewModel.selectYear(it) },
                        currencyFormat = currencyFormat
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetCompositionTab(
    statistics: com.assetmanager.core.domain.model.AssetStatistics,
    currencyFormat: NumberFormat
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "资产总额",
                    value = currencyFormat.format(statistics.totalValue),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "资产数量",
                    value = "${statistics.assetCount} 件",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "分类占比",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (statistics.categoryStats.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无数据",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        statistics.categoryStats.forEach { stat ->
                            CategoryStatItem(stat = stat, currencyFormat = currencyFormat)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStatItem(
    stat: com.assetmanager.core.domain.model.CategoryStat,
    currencyFormat: NumberFormat
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(parseColor(stat.categoryColor))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stat.categoryName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${String.format("%.1f", stat.percentage)}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (stat.percentage / 100).toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = parseColor(stat.categoryColor),
            trackColor = parseColor(stat.categoryColor).copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${currencyFormat.format(stat.totalValue)} (${stat.assetCount}件)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ValueTrendTab(
    statistics: com.assetmanager.core.domain.model.AssetStatistics,
    currencyFormat: NumberFormat
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "价值概览",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ValueOverviewRow("购买总价", statistics.totalPurchasePrice, currencyFormat)
                    Spacer(modifier = Modifier.height(8.dp))
                    ValueOverviewRow("当前总价值", statistics.totalValue, currencyFormat)
                    Spacer(modifier = Modifier.height(8.dp))
                    ValueOverviewRow(
                        "累计折旧",
                        statistics.totalDepreciated,
                        currencyFormat,
                        isNegative = true
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "平均折旧率",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val avgDepreciationRate = if (statistics.totalPurchasePrice > 0) {
                        statistics.totalDepreciated / statistics.totalPurchasePrice
                    } else 0.0

                    Text(
                        text = String.format("%.1f%%", avgDepreciationRate * 100),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ValueOverviewRow(
    label: String,
    value: Double,
    currencyFormat: NumberFormat,
    isNegative: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = currencyFormat.format(value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ConsumptionAnalysisTab(
    statistics: com.assetmanager.core.domain.model.AssetStatistics,
    monthlyStats: List<com.assetmanager.core.domain.model.MonthlyStat>,
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    currencyFormat: NumberFormat
) {
    val currentYear = java.time.LocalDate.now().year
    val years = (currentYear - 5..currentYear).toList()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years.reversed()) { year ->
                    FilterChip(
                        selected = selectedYear == year,
                        onClick = { onYearChange(year) },
                        label = { Text("$year 年") }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "$selectedYear 年消费统计",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val yearTotal = monthlyStats.sumOf { it.totalPurchasePrice }
                    val yearCount = monthlyStats.sumOf { it.assetCount }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "年度总消费",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currencyFormat.format(yearTotal),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "资产数量",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$yearCount 件",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        items(monthlyStats.filter { it.totalPurchasePrice > 0 }) { stat ->
            val monthName = java.time.Month.of(stat.month).toString()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${stat.month}月",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${stat.assetCount} 件资产",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = currencyFormat.format(stat.totalPurchasePrice),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
