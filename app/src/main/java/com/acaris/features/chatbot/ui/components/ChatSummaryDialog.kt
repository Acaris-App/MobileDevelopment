package com.acaris.features.chatbot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomTextField

@Composable
fun ChatSummaryDialog(
    showDialog: Boolean,
    draftSummary: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!showDialog) return

    var editedSummary by remember { mutableStateOf(draftSummary) }

    CustomDialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        confirmText = "Simpan",
        onConfirm = { onConfirm(editedSummary) },
        dismissText = "Batal",
        onDismiss = onDismiss,
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Kesimpulan Konsultasi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Aca merangkum poin bimbinganmu seperti di bawah ini. Silakan edit jika ada yang kurang tepat sebelum disimpan ke riwayat.",
                    fontSize = 13.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomTextField(
                    value = editedSummary,
                    onValueChange = { editedSummary = it },
                    label = "Ringkasan",
                    placeholder = "Tuliskan kesimpulan bimbingan di sini...",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}