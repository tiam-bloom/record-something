package com.assetmanager.app.ui.pages.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.assetmanager.app.ui.components.*
import com.assetmanager.core.domain.model.Category
import com.assetmanager.app.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManageScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加分类",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.categories) { category ->
                    CategoryItem(
                        category = category,
                        onEdit = { viewModel.showEditDialog(category) },
                        onDelete = { showDeleteDialog = category }
                    )
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        CategoryDialog(
            category = uiState.editingCategory,
            onDismiss = { viewModel.hideDialog() },
            onSave = { name, icon, color, rate ->
                viewModel.saveCategory(name, icon, color, rate)
            }
        )
    }

    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除分类 \"${category.name}\" 吗？该分类下的资产将变为未分类状态。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(category)
                        showDeleteDialog = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.color).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.name),
                    contentDescription = null,
                    tint = parseColor(category.color),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "折旧率: ${String.format("%.0f%%", category.defaultDepreciationRate * 100)}/年",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑"
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(category?.icon ?: "category") }
    var selectedColor by remember { mutableStateOf(category?.color ?: "#4CAF50") }
    var depreciationRate by remember { mutableStateOf(category?.defaultDepreciationRate?.toString() ?: "10") }
    var showIconPicker by remember { mutableStateOf(false) }

    val icons = listOf(
        "phone_android" to Icons.Default.PhoneAndroid,
        "computer" to Icons.Default.Computer,
        "home" to Icons.Default.Home,
        "camera_alt" to Icons.Default.CameraAlt,
        "sports_esports" to Icons.Default.SportsEsports,
        "chair" to Icons.Default.Chair,
        "print" to Icons.Default.Print,
        "directions_car" to Icons.Default.DirectionsCar,
        "diamond" to Icons.Default.Diamond,
        "category" to Icons.Default.Category
    )

    val colors = listOf(
        "#4CAF50", "#2196F3", "#FF9800", "#9C27B0",
        "#E91E63", "#795548", "#607D8B", "#F44336",
        "#FFD700", "#9E9E9E"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "添加分类" else "编辑分类") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("图标", style = MaterialTheme.typography.bodyMedium)
                    IconButton(onClick = { showIconPicker = !showIconPicker }) {
                        Icon(
                            imageVector = icons.find { it.first == selectedIcon }?.second ?: Icons.Default.Category,
                            contentDescription = "选择图标",
                            tint = parseColor(selectedColor)
                        )
                    }
                }

                if (showIconPicker) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        icons.forEach { (iconName, icon) ->
                            IconButton(
                                onClick = {
                                    selectedIcon = iconName
                                    showIconPicker = false
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (selectedIcon == iconName)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    )
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = iconName
                                )
                            }
                        }
                    }
                }

                Text("颜色", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parseColor(color))
                                .then(
                                    if (selectedColor == color) {
                                        Modifier.padding(4.dp)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clip(CircleShape)
                                .background(
                                    if (selectedColor == color)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        parseColor(color)
                                )
                                .then(
                                    Modifier
                                        .padding(if (selectedColor == color) 4.dp else 0.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(color))
                                )
                        ) {
                            if (selectedColor == color) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "已选择",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = depreciationRate,
                    onValueChange = { depreciationRate = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("年折旧率 (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, selectedIcon, selectedColor, depreciationRate.toDoubleOrNull()?.div(100) ?: 0.1)
                    }
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
