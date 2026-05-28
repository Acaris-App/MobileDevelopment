package com.acaris.features.chatbot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // 🌟 Tambahan import Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog // 🌟 Tambahan import CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.chatbot.presentation.viewmodel.ChatbotViewModel
import com.acaris.features.chatbot.ui.components.ChatBubble
import com.acaris.features.chatbot.ui.components.ChatInputBar
import com.acaris.features.chatbot.ui.components.ChatSummaryDialog
import com.acaris.features.chatbot.ui.components.TypingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatbotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var showEndSessionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadActiveSession()
    }

    LaunchedEffect(uiState.messages.size, uiState.isSending) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    if (showEndSessionDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showEndSessionDialog = false },
            confirmText = "Akhiri",
            dismissText = "Batal",
            onConfirm = {
                showEndSessionDialog = false
                viewModel.endSession()
            },
            onDismiss = { showEndSessionDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Akhiri Sesi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Apakah Anda yakin ingin mengakhiri sesi percakapan ini? Aca akan membuatkan ringkasan untuk Anda.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (uiState.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.dismissDialogs() }, // Memanggil fungsi dismissDialogs di ViewModel
            confirmText = "Tutup",
            onConfirm = { viewModel.dismissDialogs() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pesan Gagal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "Terjadi kesalahan saat mengirim pesan ke Aca.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ACA", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                },
                navigationIcon = {},
                actions = {
                    // 🌟 FIX: Ikon Robot sekarang memicu dialog konfirmasi
                    IconButton(onClick = { showEndSessionDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Akhiri Sesi",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.messages.isEmpty() && !uiState.isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🤖\nHalo! Ada yang bisa Aca bantu?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tanyakan seputar format dokumen, alur bimbingan skripsi, atau administrasi akademik lainnya.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            ChatBubble(message = message)
                        }

                        if (uiState.isSending) {
                            item {
                                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = "Aca sedang mengetik",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            TypingIndicator() // 🌟 PANGGIL ANIMASINYA DI SINI!
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ChatInputBar(
                onSendMessage = { viewModel.sendMessage(it) },
                enabled = !uiState.isLoading && !uiState.isGeneratingSummary,
                modifier = Modifier.fillMaxWidth()
            )

            if (!isKeyboardVisible) {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    ChatSummaryDialog(
        showDialog = uiState.showSummaryDialog,
        draftSummary = uiState.draftSummary,
        onDismiss = { viewModel.dismissDialogs() },
        onConfirm = { finalText -> viewModel.submitFinalSummary(finalText) }
    )

    if (uiState.isLoading || uiState.isGeneratingSummary) {
        CustomLoadingOverlay(isLoading = true)
    }
}