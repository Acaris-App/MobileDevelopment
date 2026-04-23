package com.acaris.features.schedule.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val dailySchedules = uiState.dailySchedules

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var selectedDateStr by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    val selectedDate = LocalDate.parse(selectedDateStr)
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
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
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> selectedDateStr = newDate.toString() },
                    currentMonth = currentMonth,
                    onMonthChanged = { newMonth -> currentMonth = newMonth },
                    scheduleStatusMap = uiState.monthlyScheduleMap
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.isLoading && dailySchedules.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                } else if (dailySchedules.isEmpty()) {
                    Text(
                        text = "Tidak ada jadwal bimbingan di tanggal ini.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    dailySchedules.forEach { schedule ->
                        ScheduleDetailCard(
                            dateStr = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                            timeSpan = schedule.time,
                            quotaInfo = schedule.quotaInfo,
                            keteranganDosen = schedule.keterangan,
                            isFull = schedule.isFull,
                            bookedStudents = schedule.bookedStudents,
                            onEditClick = {
                                activeSchedule = schedule
                                isEditMode = true
                                showBottomSheet = true
                            },
                            onDeleteClick = {
                                if (schedule.bookedStudents.isNotEmpty()) {
                                    Toast.makeText(context, "Gagal: Jadwal sudah di-booking mahasiswa!", Toast.LENGTH_LONG).show()
                                } else {
                                    activeSchedule = schedule
                                    showDeleteDialog = true
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

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

                Spacer(modifier = Modifier.height(64.dp))
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

            if (uiState.isLoading && dailySchedules.isNotEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // DIALOG HAPUS
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

            // 🌟 DIALOG SUKSES
            if (showSuccessDialog) {
                CustomDialog(
                    showDialog = true,
                    onDismissRequest = { showSuccessDialog = false },
                    confirmText = "Tutup",
                    onConfirm = { showSuccessDialog = false },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Berhasil!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Jadwal bimbingan telah berhasil diperbarui di sistem.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        }
    }
}