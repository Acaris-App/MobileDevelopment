package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.features.chatbot.presentation.model.ChatHistoryItemUiModel
import com.acaris.features.chatbot.ui.components.ChatbotHistoryItemCard

@Composable
fun ChatbotRiwayatSection(
    historyList: List<ChatHistoryItemUiModel>,
    onItemClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    if (historyList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada riwayat bimbingan dengan Aca.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            historyList.forEach { item ->
                ChatbotHistoryItemCard(
                    title = item.title,
                    date = item.date,
                    status = item.status,
                    onClick = { onItemClick(item.sessionId) }
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}