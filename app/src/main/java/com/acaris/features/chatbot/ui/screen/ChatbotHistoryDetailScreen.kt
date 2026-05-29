package com.acaris.features.chatbot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.chatbot.presentation.viewmodel.ChatbotHistoryViewModel
import com.acaris.features.chatbot.ui.components.ChatBubble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotHistoryDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatbotHistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Otomatis scroll ke bawah agar pesan terakhir terlihat
    LaunchedEffect(uiState.selectedSessionMessages.size) {
        if (uiState.selectedSessionMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.selectedSessionMessages.size - 1)
        }
    }

    // 🌟 POP UP UNTUK MELIHAT RINGKASAN
    if (uiState.showSummaryDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.toggleSummaryDialog(false) },
            confirmText = "Tutup",
            onConfirm = { viewModel.toggleSummaryDialog(false) },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Notes,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ringkasan Bimbingan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Box agar teks ringkasan terlihat rapi
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.selectedSessionSummary,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Obrolan", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // 🌟 FIX: Menggunakan CustomBackButton dari Acaris
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        CustomBackButton(
                            onClick = {
                                viewModel.clearDetailState()
                                onNavigateBack()
                            }
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        CustomCircularIconButton(
                            icon = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = "Lihat Ringkasan",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp),
                            onClick = { viewModel.toggleSummaryDialog(true) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = 32.dp // Tidak butuh ruang besar karena tidak ada input text di bawah
                )
            ) {
                items(uiState.selectedSessionMessages, key = { it.id }) { message ->
                    // 🌟 MENGGUNAKAN ULANG CHAT BUBBLE DARI FITUR SEBELUMNYA!
                    ChatBubble(message = message)
                }
            }
        }
    }

    if (uiState.isLoadingDetail) {
        CustomLoadingOverlay(isLoading = true)
    }
}