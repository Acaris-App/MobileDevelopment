package com.acaris.features.chatbot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomDialog

@Composable
fun ChatSummaryDialog(
    showDialog: Boolean,
    draftSummary: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!showDialog) return

    var editedSummary by remember { mutableStateOf(draftSummary) }

    // 🌟 REVISI: Sesuaikan langsung dengan parameter CustomDialog Core milik Kapten
    CustomDialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        confirmText = "Simpan",
        onConfirm = { onConfirm(editedSummary) },
        dismissText = "Batal",
        onDismiss = onDismiss,
        content = {
            // Content di sini fokus ke teks informasi dan kolom edit saja,
            // karena tombol aksi & padding luar sudah diurus otomatis oleh core dialog kita!
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

                OutlinedTextField(
                    value = editedSummary,
                    onValueChange = { editedSummary = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Tuliskan kesimpulan bimbingan di sini...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }
        }
    )
}