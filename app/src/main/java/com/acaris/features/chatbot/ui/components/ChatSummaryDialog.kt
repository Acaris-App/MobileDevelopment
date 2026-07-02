package com.acaris.features.chatbot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.acaris.core.ui.components.CustomTextField
import com.acaris.core.ui.components.glowShadow

@Composable
fun ChatSummaryDialog(
    showDialog: Boolean,
    summaryText: String,
    isReadOnly: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: ((String) -> Unit)? = null
) {
    if (!showDialog) return

    var editedSummary by remember(summaryText) { mutableStateOf(summaryText) }

    CustomDialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        confirmText = if (isReadOnly) "Tutup" else "Simpan",
        onConfirm = {
            if (isReadOnly) {
                onDismiss()
            } else {
                onConfirm?.invoke(editedSummary)
            }
        },
        dismissText = if (isReadOnly) null else "Batal",
        onDismiss = if (isReadOnly) null else onDismiss,
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

                if (isReadOnly) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glowShadow(
                                color = MaterialTheme.colorScheme.secondary,
                                alpha = 0.8f,
                                blurRadius = 6.dp,
                                borderRadius = 12.dp
                            )
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = summaryText.ifBlank { "Belum ada ringkasan bimbingan." },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start
                        )
                    }
                } else {
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
        }
    )
}