package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.monitoring_mahasiswa.presentation.model.SortOption
import com.acaris.features.monitoring_mahasiswa.presentation.viewmodel.MonitoringViewModel
import com.acaris.features.monitoring_mahasiswa.ui.components.MahasiswaItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringListScreen(
    viewModel: MonitoringViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDaftarMahasiswa()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mahasiswa Bimbingan",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Cari Nama atau NPM...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box {
                    CustomCircularIconButton(
                        icon = Icons.Outlined.FilterList,
                        contentDescription = "Urutkan",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(40.dp),
                        onClick = { isDropdownExpanded = true }
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        fontWeight = if (uiState.sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                        color = if (uiState.sortOption == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.onSortOptionChanged(option)
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.filteredListMahasiswa.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "Mahasiswa tidak ditemukan." else "Belum ada mahasiswa bimbingan.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredListMahasiswa) { mahasiswa ->
                            MahasiswaItemCard(
                                mahasiswa = mahasiswa,
                                onClick = { onNavigateToDetail(mahasiswa.id) }
                            )
                        }
                    }
                }

                if (uiState.errorMessage != null && uiState.listMahasiswa.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.errorMessage ?: "Terjadi kesalahan", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchDaftarMahasiswa() }) {
                            Text("Coba Lagi")
                        }
                    }
                }

                if (uiState.isLoading) {
                    CustomLoadingOverlay(isLoading = true)
                }
            }
        }
    }
}