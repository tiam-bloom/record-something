package com.assetmanager.app.ui.pages.assets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.assetmanager.app.ui.components.LoadingIndicator
import com.assetmanager.core.domain.model.DepreciationMethod
import com.assetmanager.app.viewmodel.AssetFormViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: AssetFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showDepreciationMethodDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditMode) "编辑资产" else "添加资产",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("资产名称 *") },
                    placeholder = { Text("请输入资产名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.errorMessage?.contains("名称") == true
                )

                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = uiState.categories.find { it.id == uiState.categoryId }?.name ?: "选择类别",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("资产类别") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("未分类") },
                            onClick = {
                                viewModel.updateCategoryId(null)
                                showCategoryDropdown = false
                            }
                        )
                        uiState.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    viewModel.updateCategoryId(category.id)
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.brand,
                        onValueChange = { viewModel.updateBrand(it) },
                        label = { Text("品牌") },
                        placeholder = { Text("如：苹果") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.model,
                        onValueChange = { viewModel.updateModel(it) },
                        label = { Text("型号") },
                        placeholder = { Text("如：iPhone 15") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = uiState.purchaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("购买日期 *") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "选择日期"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                OutlinedTextField(
                    value = uiState.purchasePrice,
                    onValueChange = { viewModel.updatePurchasePrice(it) },
                    label = { Text("购买价格 *") },
                    placeholder = { Text("请输入购买价格") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    leadingIcon = { Text("¥") },
                    isError = uiState.errorMessage?.contains("价格") == true
                )

                ExposedDropdownMenuBox(
                    expanded = showDepreciationMethodDropdown,
                    onExpandedChange = { showDepreciationMethodDropdown = it }
                ) {
                    OutlinedTextField(
                        value = uiState.depreciationMethod.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("折旧方式") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDepreciationMethodDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showDepreciationMethodDropdown,
                        onDismissRequest = { showDepreciationMethodDropdown = false }
                    ) {
                        DepreciationMethod.entries.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method.displayName) },
                                onClick = {
                                    viewModel.updateDepreciationMethod(method)
                                    showDepreciationMethodDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.designLifeYears,
                    onValueChange = { viewModel.updateDesignLifeYears(it) },
                    label = { Text("设计使用寿命（年）") },
                    placeholder = { Text("可选，如：5") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.updateNotes(it) },
                    label = { Text("备注") },
                    placeholder = { Text("可选，添加备注信息") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.saveAsset() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (viewModel.isEditMode) "保存修改" else "添加资产",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.purchaseDate
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.updatePurchaseDate(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
