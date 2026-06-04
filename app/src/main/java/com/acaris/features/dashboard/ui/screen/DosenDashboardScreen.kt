package com.acaris.features.dashboard.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
import com.acaris.features.dashboard.presentation.viewmodel.DosenDashboardViewModel
import com.acaris.features.dashboard.ui.components.DashboardStatCard
import com.acaris.features.dashboard.ui.components.JadwalMingguIniCard
import com.acaris.features.dashboard.ui.components.LeaderboardItemData
import com.acaris.features.dashboard.ui.components.LeaderboardSection
import com.acaris.features.schedule.ui.components.CustomCalendar
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenDashboardScreen(
    onNavigateToSchedule: (String) -> Unit,
    onNavigateToMonitoring: () -> Unit,
    viewModel: DosenDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val scrollState = rememberScrollState()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // 🌟 STATE BARU: Untuk melacak status Zoom Gambar
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
                                    text = uiState.errorMessage ?: "Terjadi kesalahan",
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
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    model = data.fotoDosen.ifEmpty { null },
                                    contentDescription = "Foto Dosen",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                        .clickable { showZoomedImage = true } // 🌟 AKSI KLIK FOTO
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = data.namaDosen,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = data.nipDosen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(data.kodeKelas))
                                            android.widget.Toast.makeText(context, "Kode Kelas disalin", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Text(
                                        text = "Kode Kelas:\n${data.kodeKelas}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.Groups,
                                title = "Mhs Bimbingan",
                                value = data.jumlahMahasiswaBimbingan,
                                iconColor = Color(0xFF4CAF50),
                                onClick = onNavigateToMonitoring
                            )
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.Today,
                                title = "Jadwal Hari Ini",
                                value = data.bimbinganHariIni,
                                iconColor = Color(0xFFFF9800)
                            )
                            DashboardStatCard(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                icon = Icons.Default.History,
                                title = "Bimbingan Smt Ini",
                                value = data.bimbinganSemesterIni
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Kalender Bimbingan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomCalendar(
                        selectedDate = LocalDate.now(),
                        onDateSelected = { localDate -> onNavigateToSchedule(localDate.toString()) },
                        currentMonth = currentMonth,
                        onMonthChanged = { viewModel.onMonthChanged(it) },
                        scheduleStatusMap = data.kalenderBimbinganMap
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ketuk tanggal untuk melihat atau mengatur jadwal.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Jadwal Minggu Ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (data.jadwalMingguIni.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada jadwal dalam 7 hari ke depan.", color =  MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            data.jadwalMingguIni.forEach { jadwal ->
                                JadwalMingguIniCard(
                                    jadwal = jadwal,
                                    onClick = {
                                        onNavigateToSchedule(jadwal.date)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    LeaderboardSection(
                        title = "Top 5 Mahasiswa (Bimbingan Teraktif)",
                        icon = Icons.Default.TrendingUp,
                        iconColor = Color(0xFF4CAF50),
                        items = data.topMahasiswaBimbingan.map { LeaderboardItemData(it.nama, "NPM: ${it.npm}", it.total) },
                        emptyMessage = "Belum ada data bimbingan mahasiswa."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LeaderboardSection(
                        title = "Top 5 Pengguna Chatbot",
                        icon = Icons.Default.SmartToy,
                        iconColor = Color(0xFF9C27B0),
                        items = data.topMahasiswaChatbot.map { LeaderboardItemData(it.nama, "NPM: ${it.npm}", it.total) },
                        emptyMessage = "Belum ada data interaksi chatbot."
                    )

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // 🌟 PANGGIL KOMPONEN DIALOG MENGGUNAKAN BLOK IF
            if (showZoomedImage && uiState.dashboardData != null) {
                CustomImageZoomDialog(
                    imageUrl = uiState.dashboardData?.fotoDosen?.ifEmpty { null },
                    onDismissRequest = { showZoomedImage = false }
                )
            }

            if (uiState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}