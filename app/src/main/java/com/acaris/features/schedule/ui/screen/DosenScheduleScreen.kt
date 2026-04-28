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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.acaris.features.schedule.presentation.viewmodel.ScheduleViewModel
import com.acaris.features.schedule.ui.components.ScheduleDetailCard
import com.acaris.features.schedule.ui.components.AddScheduleBottomSheet
import com.acaris.features.schedule.ui.components.CustomCalendar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun DosenScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dailySchedules = uiState.dailySchedules

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var selectedDateStr by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    val selectedDate = LocalDate.parse(selectedDateStr)
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var activeSchedule by remember { mutableStateOf<ScheduleUiModel?>(null) }

    LaunchedEffect(currentMonth) {
        viewModel.fetchMonthlySchedules(currentMonth.year, currentMonth.monthValue)
    }

    LaunchedEffect(selectedDateStr) {
        viewModel.fetchDailySchedule(selectedDateStr)
    }

    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess) {
            showBottomSheet = false
            showSuccessDialog = true

            viewModel.fetchMonthlySchedules(currentMonth.year, currentMonth.monthValue)
            viewModel.fetchDailySchedule(selectedDateStr)
            viewModel.resetState()
        }
        if (uiState.errorMessage != null) {
            showErrorDialog = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 24.dp,
                    bottom = 120.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    CustomCalendar(
                        selectedDate = selectedDate,
                        onDateSelected = { newDate -> selectedDateStr = newDate.toString() },
                        currentMonth = currentMonth,
                        onMonthChanged = { newMonth -> currentMonth = newMonth },
                        scheduleStatusMap = uiState.monthlyScheduleMap
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Jadwal Tanggal ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (uiState.isLoading && dailySchedules.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (dailySchedules.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak ada jadwal bimbingan di tanggal ini.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                } else {
                    items(dailySchedules) { schedule ->
                        ScheduleDetailCard(
                            timeSpan = schedule.time,
                            quotaInfo = schedule.quotaInfo,
                            keteranganDosen = schedule.keterangan,
                            isFull = schedule.isFull,
                            isSelesai = schedule.isSelesai,
                            bookedStudents = schedule.bookedStudents,
                            onEditClick = {
                                activeSchedule = schedule
                                isEditMode = true
                                showBottomSheet = true
                            },
                            onDeleteClick = {
                                if (schedule.bookedStudents.isNotEmpty()) {
                                    viewModel.resetState()
                                    showErrorDialog = true
                                } else {
                                    activeSchedule = schedule
                                    showDeleteDialog = true
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            activeSchedule = null
                            isEditMode = false
                            showBottomSheet = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Tambah Jadwal Baru")
                    }
                }
            }

            if (uiState.isLoading && dailySchedules.isNotEmpty()) {
                CustomLoadingOverlay(isLoading = true)
            }

            if (showBottomSheet) {
                AddScheduleBottomSheet(
                    selectedDate = selectedDateStr,
                    isEditMode = isEditMode,
                    initialStartTime = if (isEditMode) activeSchedule?.rawStartTime ?: "" else "",
                    initialEndTime = if (isEditMode) activeSchedule?.rawEndTime ?: "" else "",
                    initialQuota = if (isEditMode) activeSchedule?.rawQuota?.toString() ?: "" else "",
                    initialKeterangan = if (isEditMode) activeSchedule?.keterangan ?: "" else "",
                    onDismiss = { showBottomSheet = false },
                    onSubmit = { startTime, endTime, quota, keterangan ->
                        if (isEditMode && activeSchedule != null) {
                            viewModel.updateSchedule(id = activeSchedule!!.id, date = selectedDateStr, startTime = startTime, endTime = endTime, quota = quota, keterangan = keterangan)
                        } else {
                            viewModel.createSchedule(date = selectedDateStr, startTime = startTime, endTime = endTime, quota = quota, keterangan = keterangan)
                        }
                    }
                )
            }

            if (showDeleteDialog && activeSchedule != null) {
                CustomDialog(
                    showDialog = true,
                    onDismissRequest = { showDeleteDialog = false },
                    confirmText = "Hapus",
                    onConfirm = {
                        showDeleteDialog = false
                        viewModel.deleteSchedule(id = activeSchedule!!.id, date = selectedDateStr, year = currentMonth.year, month = currentMonth.monthValue)
                    },
                    dismissText = "Batal",
                    onDismiss = { showDeleteDialog = false },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Hapus Jadwal?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Apakah Anda yakin ingin menghapus jadwal pukul ${activeSchedule!!.time}?", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }

            if (showErrorDialog) {
                CustomDialog(
                    showDialog = true,
                    onDismissRequest = { showErrorDialog = false; viewModel.resetState() },
                    confirmText = "Tutup",
                    onConfirm = { showErrorDialog = false; viewModel.resetState() },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Terjadi Kesalahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val msg = uiState.errorMessage ?: "Jadwal ini sudah di-booking mahasiswa. Tidak bisa dihapus!"
                            Text(msg, textAlign = TextAlign.Center, color = Color.Gray)
                        }
                    }
                )
            }

            if (showSuccessDialog) {
                CustomDialog(
                    showDialog = true,
                    onDismissRequest = { showSuccessDialog = false },
                    confirmText = "Tutup",
                    onConfirm = { showSuccessDialog = false },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(72.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Berhasil!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Jadwal bimbingan telah berhasil diperbarui di sistem.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        }
    }
}