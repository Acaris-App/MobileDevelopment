package com.acaris.features.schedule.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.schedule.presentation.model.ScheduleUiModel
import com.acaris.features.schedule.presentation.viewmodel.MahasiswaScheduleViewModel
import com.acaris.features.schedule.ui.components.BookingBottomSheet
import com.acaris.features.schedule.ui.components.CustomCalendar
import com.acaris.features.schedule.ui.components.MahasiswaScheduleCard
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahasiswaScheduleScreen(
    initialSelectedDate: String? = null,
    viewModel: MahasiswaScheduleViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(initialSelectedDate) {
        if (initialSelectedDate != null) {
            try {
                val parsedDate = LocalDate.parse(initialSelectedDate)
                selectedDate = parsedDate
                currentMonth = YearMonth.from(parsedDate)
            } catch (e: Exception) {
            }
        }
    }

    var selectedScheduleToBook by remember { mutableStateOf<ScheduleUiModel?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentMonth) {
        viewModel.fetchMonthlySchedules(currentMonth.year, currentMonth.monthValue)
    }

    LaunchedEffect(selectedDate) {
        viewModel.fetchDailySchedule(selectedDate.toString())
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) { showSuccessDialog = true }
    }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            isRefreshing = false
        }
    }

    if (showSuccessDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.resetState(); showSuccessDialog = false },
            confirmText = "Tutup",
            onConfirm = { viewModel.resetState(); showSuccessDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Booking Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jadwal bimbingan Anda telah berhasil didaftarkan.", textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
        )
    }

    if (state.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.resetState() },
            confirmText = "Tutup",
            onConfirm = { viewModel.resetState() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Terjadi Kesalahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(state.errorMessage ?: "", textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
        )
    }

    selectedScheduleToBook?.let { schedule ->
        BookingBottomSheet(
            schedule = schedule,
            onDismiss = { selectedScheduleToBook = null },
            onSubmit = { agenda ->
                viewModel.bookSchedule(
                    scheduleId = schedule.id,
                    agenda = agenda,
                    selectedDate = selectedDate.toString(),
                    year = currentMonth.year,
                    month = currentMonth.monthValue
                )
                selectedScheduleToBook = null
            }
        )
    }

    Scaffold { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchMonthlySchedules(currentMonth.year, currentMonth.monthValue)
                viewModel.fetchDailySchedule(selectedDate.toString())
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 120.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CustomCalendar(
                            selectedDate = selectedDate,
                            onDateSelected = { date -> selectedDate = date },
                            currentMonth = currentMonth,
                            onMonthChanged = { newMonth -> currentMonth = newMonth },
                            scheduleStatusMap = state.monthlyScheduleMap
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        val displayDate = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID")))
                        Text(
                            text = "Jadwal Tersedia ($displayDate)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.dailySchedules.isEmpty() && !state.isLoading && !isRefreshing) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                Text("Tidak ada jadwal pada tanggal ini.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(state.dailySchedules) { schedule ->
                            MahasiswaScheduleCard(
                                schedule = schedule,
                                onClick = {
                                    if (!schedule.isBookedByMe && !schedule.isFull) {
                                        selectedScheduleToBook = schedule
                                    }
                                }
                            )
                        }
                    }
                }

                if (state.isLoading && !isRefreshing) {
                    CustomLoadingOverlay(isLoading = true)
                }
            }
        }
    }
}