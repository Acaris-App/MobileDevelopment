package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
// 🌟 IMPORT SHARED COMPONENT DAN MODEL DARI FITUR DOCUMENTS MAHASISWA
import com.acaris.features.documents_mahasiswa.presentation.model.SharedDocumentUiModel
import com.acaris.features.documents_mahasiswa.ui.components.SharedDocumentManager
import com.acaris.features.monitoring_mahasiswa.presentation.viewmodel.MonitoringViewModel
import com.acaris.features.monitoring_mahasiswa.ui.components.DetailProfilMahasiswaCard
import com.acaris.features.monitoring_mahasiswa.ui.components.RiwayatBimbinganCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringDetailScreen(
    mahasiswaId: String,
    onNavigateBack: () -> Unit,
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
                            // TAB 2: DOKUMEN MAHASISWA (MENGGUNAKAN SHARED COMPONENT)

                            // 1. Ekstrak type dan semester dari 'title'
                            // (Contoh title: "KRS Semester 6" atau "Transkrip Nilai")
                            val mappedDocuments = detail.dokumen.map { doc ->
                                val typeLowerCase = when {
                                    doc.title.contains("KRS", ignoreCase = true) -> "krs"
                                    doc.title.contains("KHS", ignoreCase = true) -> "khs"
                                    doc.title.contains("Transkrip", ignoreCase = true) -> "transkrip"
                                    else -> "unknown"
                                }

                                // Mencari angka setelah kata "Semester"
                                val semesterMatch = Regex("Semester\\s+(\\d+)", RegexOption.IGNORE_CASE).find(doc.title)
                                val extractedSemester = semesterMatch?.groupValues?.get(1)?.toIntOrNull()

                                SharedDocumentUiModel(
                                    id = doc.id,
                                    type = typeLowerCase,
                                    semester = extractedSemester,
                                    fileUrl = doc.fileUrl,
                                    uploadedAt = doc.uploadedAt
                                )
                            }

                            // 2. Ekstrak current semester dari 'infoAkademik'
                            // (Contoh infoAkademik: "Angkatan 2021 • Semester 6")
                            val currentSemesterMatch = Regex("Semester\\s+(\\d+)", RegexOption.IGNORE_CASE).find(detail.infoAkademik)
                            val currentSemester = currentSemesterMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1

                            // 3. Panggil Komponen Shared dengan isReadOnly = true
                            SharedDocumentManager(
                                documents = mappedDocuments,
                                currentSemester = currentSemester,
                                isReadOnly = true, // DOSEN HANYA BISA MELIHAT
                                onViewDocument = { url ->
                                    if (url.isNotBlank()) uriHandler.openUri(url)
                                }
                            )
                        }
                        2 -> {
                            // 🌟 TAB 3: RIWAYAT BIMBINGAN
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
                            // TAB 4: RIWAYAT CHATBOT
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