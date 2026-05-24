package com.acaris.features.dashboard.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.dashboard.presentation.viewmodel.AdminDashboardViewModel
import com.acaris.features.dashboard.ui.components.DashboardStatCard
import com.acaris.features.dashboard.ui.components.LeaderboardItemData
import com.acaris.features.dashboard.ui.components.LeaderboardSection

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
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

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = data.fotoAdmin.ifEmpty { null },
                                contentDescription = "Foto Admin",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = data.namaAdmin,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "NIP: ${data.nipAdmin}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Statistik Global", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DashboardStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Groups,
                                title = "Total Mahasiswa",
                                value = data.totalMahasiswa,
                                iconColor = Color(0xFF4CAF50)
                            )
                            DashboardStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.School,
                                title = "Total Dosen",
                                value = data.totalDosen,
                                iconColor = Color(0xFF2196F3)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DashboardStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.HistoryEdu,
                                title = "Sesi Bimbingan",
                                value = data.totalBimbingan,
                                iconColor = Color(0xFFFF9800)
                            )
                            DashboardStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.SmartToy,
                                title = "Sesi Chatbot",
                                value = data.totalChatbot,
                                iconColor = Color(0xFF9C27B0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    LeaderboardSection(
                        title = "Top 5 Dosen (Bimbingan Terbanyak)",
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFFFC107),
                        items = data.topDosenBimbingan.map { LeaderboardItemData(it.nama, "NIP: ${it.nip}", it.total) },
                        emptyMessage = "Belum ada data bimbingan dosen."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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

            if (uiState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}