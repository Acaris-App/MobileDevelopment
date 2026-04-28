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
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.features.monitoring_mahasiswa.presentation.viewmodel.MonitoringViewModel
import com.acaris.features.monitoring_mahasiswa.ui.components.DetailProfilMahasiswaCard
import com.acaris.features.monitoring_mahasiswa.ui.components.DokumenMahasiswaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDetailScreen(
    mahasiswaId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHistoryBimbingan: (String) -> Unit,
    onNavigateToHistoryChatbot: (String) -> Unit,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(mahasiswaId) {
        viewModel.fetchDetailMahasiswa(mahasiswaId)
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

                    // 🌟 CARD 1: DATA DIRI MAHASISWA
                    DetailProfilMahasiswaCard(detail = detail)

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🌟 CARD 2: DOKUMEN MAHASISWA
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // 🌟 TOMBOL AKSI
                    CustomPrimaryButton(
                        text = "Riwayat Bimbingan",
                        onClick = { onNavigateToHistoryBimbingan(detail.id) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomOutlinedButton(
                        text = "Riwayat Chatbot Mahasiswa",
                        onClick = { onNavigateToHistoryChatbot(detail.id) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(40.dp)) // Jarak bernafas di bawah
                }
            }

            // Pesan Error
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

            // Loading Overlay
            if (uiState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}