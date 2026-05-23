package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomOutlinedButton
import com.acaris.features.monitoring_mahasiswa.presentation.viewmodel.MonitoringViewModel
import com.acaris.features.monitoring_mahasiswa.ui.components.DetailProfilMahasiswaCard
import com.acaris.features.monitoring_mahasiswa.ui.components.DokumenMahasiswaCard
import com.acaris.features.monitoring_mahasiswa.ui.components.RiwayatBimbinganCard // 🌟 IMPORT CARD RIWAYAT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDetailScreen(
    mahasiswaId: String,
    onNavigateBack: () -> Unit,
    // onNavigateToHistoryBimbingan dihapus karena sudah masuk ke tab!
    onNavigateToHistoryChatbot: (String) -> Unit,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Data Diri", "Dokumen", "Bimbingan", "Chatbot")

    // 🌟 KITA FETCH DATA DETAIL DAN RIWAYAT SEKALIGUS
    LaunchedEffect(mahasiswaId) {
        viewModel.fetchDetailMahasiswa(mahasiswaId)
        viewModel.fetchHistoryBimbingan(mahasiswaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Mahasiswa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    CustomBackButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            uiState.detailMahasiswa?.let { detail ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        edgePadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    when (selectedTabIndex) {
                        0 -> {
                            // TAB 1: DATA DIRI MAHASISWA
                            DetailProfilMahasiswaCard(detail = detail)
                        }
                        1 -> {
                            // TAB 2: DOKUMEN MAHASISWA
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Box(modifier = Modifier.padding(24.dp)) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text("Dokumen Akademik", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(16.dp))

                                        if (detail.dokumen.isEmpty()) {
                                            Text("Mahasiswa belum mengunggah dokumen.", color = Color.Gray)
                                        } else {
                                            detail.dokumen.forEach { doc ->
                                                DokumenMahasiswaCard(
                                                    dokumen = doc,
                                                    onClick = {
                                                        if (doc.fileUrl.isNotBlank()) {
                                                            uriHandler.openUri(doc.fileUrl)
                                                        }
                                                    }
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // 🌟 TAB 3: RIWAYAT BIMBINGAN (LANGSUNG TAMPIL DI SINI)
                            if (uiState.historyList.isEmpty() && !uiState.isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Belum ada riwayat bimbingan.",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    uiState.historyList.forEach { history ->
                                        RiwayatBimbinganCard(riwayat = history)
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                        3 -> {
                            // TAB 4: RIWAYAT CHATBOT (Sementara masih pakai tombol)
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Menu Riwayat Chatbot dipindahkan ke sini.", color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))
                                CustomOutlinedButton(
                                    text = "Buka Riwayat Chatbot",
                                    onClick = { onNavigateToHistoryChatbot(detail.id) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            if (uiState.errorMessage != null && uiState.detailMahasiswa == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.errorMessage ?: "Terjadi kesalahan", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchDetailMahasiswa(mahasiswaId) }) {
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