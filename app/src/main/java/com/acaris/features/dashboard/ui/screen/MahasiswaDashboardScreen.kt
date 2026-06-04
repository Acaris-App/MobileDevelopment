package com.acaris.features.dashboard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomImageZoomDialog // 🌟 IMPORT KOMPONEN ZOOM
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.dashboard.presentation.viewmodel.MahasiswaDashboardViewModel
import com.acaris.features.dashboard.ui.components.DashboardStatCard
import com.acaris.features.dashboard.ui.components.UpcomingBimbinganCard
import com.acaris.features.schedule.ui.components.CustomCalendar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahasiswaDashboardScreen(
    onNavigateToSchedule: (String) -> Unit,
    onNavigateToHistoryBimbingan: () -> Unit,
    onNavigateToHistoryChatbot: () -> Unit,
    viewModel: MahasiswaDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val scrollState = rememberScrollState()

    // 🌟 STATE BARU: Untuk melacak gambar mana yang mau di-zoom (Mahasiswa atau Dosen)
    var zoomedImageUrl by remember { mutableStateOf<String?>(null) }
    var showZoomedImage by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            uiState.dashboardData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // ERROR BANNER
                    if (uiState.errorMessage != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = uiState.errorMessage ?: "Terjadi kesalahan koneksi",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.loadDashboard() }) {
                                    Text("Coba Lagi", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // KARTU MAHASISWA
                            Card(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        model = data.fotoMahasiswa.ifEmpty { null },
                                        contentDescription = "Foto Mahasiswa",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .clickable {
                                                // 🌟 Tampilkan foto mahasiswa
                                                zoomedImageUrl = data.fotoMahasiswa.ifEmpty { null }
                                                showZoomedImage = true
                                            }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = data.namaMahasiswa,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = data.npmMahasiswa,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // KARTU DOSEN PA
                            Card(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        model = data.fotoDosen.ifEmpty { null },
                                        contentDescription = "Foto Dosen",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .clickable {
                                                // 🌟 Tampilkan foto dosen
                                                zoomedImageUrl = data.fotoDosen.ifEmpty { null }
                                                showZoomedImage = true
                                            }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = data.dosenPa,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = data.nipDosen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // KOLOM KANAN: 3 Kartu Statistik
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.Stars,
                                title = "IPK",
                                value = data.ipk,
                                iconColor = Color(0xFFFF9800)
                            )
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.School,
                                title = "Semester",
                                value = data.semesterSaatIni
                            )
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.History,
                                title = "Bimbingan Smt Ini",
                                value = data.bimbinganSemesterIni
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // KARTU STATISTIK BAWAH
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AutoGraph,
                            title = "Total Bimbingan",
                            value = data.bimbinganKeseluruhan,
                            onClick = onNavigateToHistoryBimbingan
                        )
                        DashboardStatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.SmartToy,
                            title = "Chatbot (Bulan Ini)",
                            value = data.chatbotBulanIni,
                            iconColor = Color(0xFF9C27B0),
                            onClick = onNavigateToHistoryChatbot
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // KALENDER
                    Text("Kalender Bimbingan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomCalendar(
                        selectedDate = LocalDate.now(),
                        onDateSelected = { localDate ->
                            onNavigateToSchedule(localDate.toString())
                        },
                        currentMonth = currentMonth,
                        onMonthChanged = { viewModel.onMonthChanged(it) },
                        scheduleStatusMap = data.kalenderBimbinganMap
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ketuk tanggal untuk melihat atau memesan jadwal.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // JADWAL TERDEKAT
                    Text("Jadwal Terdekat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (data.jadwalTerdekat.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada jadwal bimbingan terdekat.", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        // TAMPILKAN DALAM BENTUK LIST COLUMN BERUNTUN
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            data.jadwalTerdekat.forEach { bimbingan ->
                                UpcomingBimbinganCard(
                                    jadwal = bimbingan,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onNavigateToSchedule(bimbingan.date) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // 🌟 PANGGIL KOMPONEN DIALOG MENGGUNAKAN BLOK IF
            if (showZoomedImage) {
                CustomImageZoomDialog(
                    imageUrl = zoomedImageUrl,
                    onDismissRequest = {
                        showZoomedImage = false
                        zoomedImageUrl = null
                    }
                )
            }

            if (uiState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}