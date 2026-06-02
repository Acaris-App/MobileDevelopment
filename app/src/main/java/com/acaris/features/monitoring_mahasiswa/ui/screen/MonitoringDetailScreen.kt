package com.acaris.features.monitoring_mahasiswa.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomChipTabRow
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
    // 🌟 FIX 1: Ubah Navigasi untuk menerima ID Mahasiswa dan ID Sesi
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
        // 🌟 FIX 2: Otomatis memuat riwayat chatbot saat halaman dibuka
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
                        .padding(innerPadding), // Padding dasar dari Scaffold
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // BAGIAN STICKY (TIDAK IKUT DI-SCROLL)
                    CustomChipTabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp) // Pastikan sejajar dengan konten di bawah
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // BAGIAN KONTEN (YANG BISA DI-SCROLL)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp), // Padding konten dipertahankan
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

                                val currentSemesterMatch = Regex("Semester\\s+(\\d+)", RegexOption.IGNORE_CASE).find(detail.infoAkademik)
                                val currentSemester = currentSemesterMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1

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
                                // 🌟 FIX 3: TAB 4 LANGSUNG MENAMPILKAN DAFTAR RIWAYAT CHATBOT
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
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .clickable {
                                                        // Buka halaman detail khusus dosen
                                                        onNavigateToChatbotDetail(detail.id, item.sessionId)
                                                    },
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                                border = BorderStroke(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = item.date,
                                                            fontSize = 12.sp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            fontWeight = FontWeight.SemiBold
                                                        )

                                                        val (statusText, statusColor, statusIcon) = when (item.status.lowercase()) {
                                                            "completed" -> Triple("Selesai", Color(0xFF4CAF50), Icons.Default.CheckCircle)
                                                            "active" -> Triple("Berjalan", Color(0xFF2196F3), Icons.Default.Schedule)
                                                            "failed", "error" -> Triple("Gagal", Color(0xFFF44336), Icons.Default.ErrorOutline)
                                                            else -> Triple(item.status.replaceFirstChar { it.uppercase() }, Color.Gray, Icons.Default.History)
                                                        }

                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = statusIcon,
                                                                contentDescription = null,
                                                                tint = statusColor,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(text = statusText, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Medium)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = item.title,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
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