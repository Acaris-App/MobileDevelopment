package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomDialog
import java.util.Calendar

@Composable
fun CustomTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    CustomDialog(
        showDialog = true,
        onDismissRequest = onDismiss,
        confirmText = "OK",
        onConfirm = { onConfirm(selectedHour, selectedMinute) },
        dismissText = "Batal",
        onDismiss = onDismiss,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Pilih Waktu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WheelPicker(
                            count = 24,
                            initialIndex = selectedHour,
                            onValueChanged = { selectedHour = it }
                        )

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        WheelPicker(
                            count = 60,
                            initialIndex = selectedMinute,
                            onValueChanged = { selectedMinute = it }
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    count: Int,
    initialIndex: Int,
    onValueChanged: (Int) -> Unit
) {
    val itemHeight = 44.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                onValueChanged(index)
            }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .height(itemHeight * 5)
            .width(64.dp)
    ) {
        items(2) { Spacer(modifier = Modifier.height(itemHeight)) }

        items(count) { index ->
            val isSelected by remember {
                derivedStateOf { listState.firstVisibleItemIndex == index }
            }

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", index),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        items(2) { Spacer(modifier = Modifier.height(itemHeight)) }
    }
}