package com.acaris.features.schedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleBottomSheet(
    selectedDate: String,
    isEditMode: Boolean = false,
    initialStartTime: String = "",
    initialEndTime: String = "",
    initialQuota: String = "",
    initialKeterangan: String = "",
    onDismiss: () -> Unit,
    onSubmit: (startTime: String, endTime: String, quota: Int, keterangan: String) -> Unit
) {
    var startTime by rememberSaveable { mutableStateOf(initialStartTime) }
    var endTime by rememberSaveable { mutableStateOf(initialEndTime) }
    var quota by rememberSaveable { mutableStateOf(initialQuota) }
    var keterangan by rememberSaveable { mutableStateOf(initialKeterangan) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (isEditMode) "Perbarui Jadwal Bimbingan" else "Tambah Jadwal Bimbingan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tanggal: $selectedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = startTime,
                        onValueChange = {},
                        label = "Mulai",
                        placeholder = "00:00",
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showStartTimePicker = true }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        value = endTime,
                        onValueChange = {},
                        label = "Selesai",
                        placeholder = "00:00",
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showEndTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = quota,
                onValueChange = { quota = it },
                label = "Kuota Mahasiswa",
                placeholder = "Contoh: 5",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = "Keterangan (Opsional)",
                placeholder = "contoh: ruangan x, bawa laptop ...",
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomPrimaryButton(
                text = if (isEditMode) "Simpan Perubahan" else "Simpan Jadwal",
                onClick = {
                    val quotaInt = quota.toIntOrNull() ?: 0
                    onSubmit(startTime, endTime, quotaInt, keterangan)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = startTime.isNotBlank() && endTime.isNotBlank() && quota.isNotBlank()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showStartTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                showEndTimePicker = false
            }
        )
    }
}