package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomChipTabRow
import com.acaris.features.chatbot.ui.components.ChatbotHistoryItemCard // 🌟 IMPORT KOMPONEN KITA
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
    onNavigateToChatbotDetail: (mahasiswaId: String, sessionId: String) -> Unit,
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Data Diri", "Dokumen", "Bimbingan", "Chatbot")

    LaunchedEffect(mahasiswaId) {
        viewModel.fetchDetailMahasiswa(mahasiswaId)
        viewModel.fetchHistoryBimbingan(mahasiswaId)
        viewModel.fetchChatbotHistory(mahasiswaId)
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
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomChipTabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (selectedTabIndex) {
                            0 -> {
                                DetailProfilMahasiswaCard(detail = detail)
                            }
                            1 -> {
                                val mappedDocuments = detail.dokumen.map { doc ->
                                    val typeLowerCase = when {
                                        doc.title.contains("KRS", ignoreCase = true) -> "krs"
                                        doc.title.contains("KHS", ignoreCase = true) -> "khs"
                                        doc.title.contains("Transkrip", ignoreCase = true) -> "transkrip"
                                        else -> "unknown"
                                    }

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

                                val currentSemester = detail.semester.toIntOrNull() ?: 1

                                SharedDocumentManager(
                                    documents = mappedDocuments,
                                    currentSemester = currentSemester,
                                    isReadOnly = true,
                                    onViewDocument = { url ->
                                        if (url.isNotBlank()) uriHandler.openUri(url)
                                    }
                                )
                            }
                            2 -> {
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
                                if (uiState.historyChatbotList.isEmpty() && !uiState.isLoading) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Belum ada riwayat bimbingan dengan Aca.",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 16.sp
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        uiState.historyChatbotList.forEach { item ->
                                            // 🌟 MENGGUNAKAN KOMPONEN REUSABLE! (Sangat Bersih)
                                            ChatbotHistoryItemCard(
                                                title = item.title,
                                                date = item.date,
                                                status = item.status,
                                                onClick = {
                                                    onNavigateToChatbotDetail(detail.id, item.sessionId)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
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