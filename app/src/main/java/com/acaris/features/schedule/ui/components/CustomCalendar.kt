package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.theme.StatusAvailableBg
import com.acaris.core.ui.theme.StatusBookedBg
import com.acaris.core.ui.theme.StatusBookedText
import com.acaris.core.ui.theme.StatusFullBg
import com.acaris.core.ui.theme.StatusSelesaiBg
import com.acaris.core.ui.theme.StatusSelesaiText
import com.acaris.features.schedule.presentation.model.ScheduleStatus
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    scheduleStatusMap: Map<LocalDate, ScheduleStatus> = emptyMap()
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { onMonthChanged(currentMonth.minusMonths(1)) },
                onNextMonth = { onMonthChanged(currentMonth.plusMonths(1)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DaysOfWeekHeader()

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                scheduleStatusMap = scheduleStatusMap
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
    val year = currentMonth.year

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya")
        }
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya")
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    scheduleStatusMap: Map<LocalDate, ScheduleStatus>
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

    val totalCells = daysInMonth + firstDayOfWeek - 1
    val rows = Math.ceil(totalCells / 7.0).toInt()

    var currentDay = 1

    Column {
        for (i in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 1..7) {
                    if (i == 0 && j < firstDayOfWeek) {
                        Box(modifier = Modifier.weight(1f))
                    } else if (currentDay <= daysInMonth) {
                        val date = currentMonth.atDay(currentDay)
                        CalendarCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == LocalDate.now(),
                            status = scheduleStatusMap[date] ?: ScheduleStatus.NONE,
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.weight(1f)
                        )
                        currentDay++
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    status: ScheduleStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellShape = RoundedCornerShape(12.dp)

    val backgroundColor = when (status) {
        ScheduleStatus.AVAILABLE -> StatusAvailableBg // Hijau
        ScheduleStatus.FULL -> StatusFullBg // Merah
        ScheduleStatus.BOOKED_BY_ME -> StatusBookedBg // Biru
        ScheduleStatus.SELESAI -> StatusSelesaiBg // Kuning
        ScheduleStatus.NONE -> Color.Transparent
    }

    val borderWidth = if (isSelected) 2.dp else 1.dp
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(cellShape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = cellShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                status == ScheduleStatus.BOOKED_BY_ME -> MaterialTheme.colorScheme.onSurface
                status == ScheduleStatus.SELESAI -> MaterialTheme.colorScheme.onSurface
                isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (isToday || isSelected || status == ScheduleStatus.BOOKED_BY_ME || status == ScheduleStatus.SELESAI || status == ScheduleStatus.AVAILABLE || status == ScheduleStatus.FULL) FontWeight.Bold else FontWeight.Normal
        )
    }
}