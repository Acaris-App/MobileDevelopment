package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomSearchAndSortBar
import com.acaris.core.ui.components.SortItem
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

    LaunchedEffect(Unit) {
        viewModel.fetchDaftarMahasiswa()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            val sortItems = remember {
                SortOption.values().map { SortItem(id = it.name, label = it.label) }
            }

            CustomSearchAndSortBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                searchPlaceholder = "Cari Nama atau NPM...",
                sortOptions = sortItems,
                currentSort = uiState.sortOption.name,
                onSortSelected = { selectedId ->
                    val selectedEnum = SortOption.valueOf(selectedId)
                    viewModel.onSortOptionChanged(selectedEnum)
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.filteredListMahasiswa.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "Mahasiswa tidak ditemukan." else "Belum ada mahasiswa bimbingan.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 90.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 100.dp
                        ),
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
                    CustomDialog(
                        showDialog = true,
                        onDismissRequest = {
                            viewModel.resetError()
                        },
                        content = {
                            Text(
                                text = uiState.errorMessage ?: "Tidak ada koneksi internet atau terjadi kesalahan.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        confirmText = "Coba Lagi",
                        onConfirm = {
                            viewModel.fetchDaftarMahasiswa()
                        }
                    )
                }

                if (uiState.isLoading) {
                    CustomLoadingOverlay(isLoading = true)
                }
            }
        }
    }
}