package com.acaris.features.chatbot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.chatbot.presentation.viewmodel.ChatbotHistoryViewModel
import com.acaris.features.chatbot.ui.components.ChatBubble
import com.acaris.features.chatbot.ui.components.ChatSummaryDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotHistoryDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatbotHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.selectedSessionMessages.size) {
        if (uiState.selectedSessionMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.selectedSessionMessages.size - 1)
        }
    }

    // 🌟 KOMPONEN DIALOG (Error Fixed)
    ChatSummaryDialog(
        showDialog = uiState.showSummaryDialog,
        summaryText = uiState.selectedSessionSummary,
        isReadOnly = true,
        onDismiss = { viewModel.toggleSummaryDialog(false) } // 🌟 Gunakan fungsi ViewModel
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Obrolan", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp)) {
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
                            icon = Icons.Default.Notes,
                            contentDescription = "Lihat Ringkasan",
                            color = MaterialTheme.colorScheme.primary,
                            buttonSize = 40.dp,
                            iconSize = 20.dp,
                            glowColor = MaterialTheme.colorScheme.primary,
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
                    bottom = 32.dp
                )
            ) {
                items(uiState.selectedSessionMessages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }

    if (uiState.isLoadingDetail) {
        CustomLoadingOverlay(isLoading = true)
    }
}