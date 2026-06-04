package com.acaris.features.chatbot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.features.chatbot.presentation.model.ChatMessageUiModel

// 🌟 IMPOR LIBRARY JEZIELLAGO YANG SUDAH BERHASIL DIUNDUH
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatBubble(
    message: ChatMessageUiModel,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    }
    val contentColor = if (message.isFromUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val shape = if (message.isFromUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Card(
                shape = shape,
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                // 🌟 ANTI MENGKERUT SAAT DI-SCROLL CEPAT!
                MarkdownText(
                    markdown = message.text,
                    color = contentColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                if (message.time == "Gagal kirim") {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Gagal Mengirim",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.time,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = message.time,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}