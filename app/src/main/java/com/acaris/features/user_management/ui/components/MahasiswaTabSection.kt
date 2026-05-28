package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.acaris.features.user_management.presentation.model.MahasiswaDocumentUiModel
import com.acaris.features.user_management.presentation.model.UserDetailUiState

@Composable
fun MahasiswaTabSection(
    uiState: UserDetailUiState,
    onUploadNewClick: (type: String, semester: Int?) -> Unit,
    onDocumentClick: (MahasiswaDocumentUiModel) -> Unit,
    onEditDocument: (MahasiswaDocumentUiModel) -> Unit,
    onDeleteDocument: (MahasiswaDocumentUiModel) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dokumen", "Bimbingan", "Chatbot (Aca)")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex,containerColor = Color.Transparent) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title) })
            }
        }

        when (selectedTabIndex) {
            0 -> DocumentListSection(
                krs = uiState.krsDocuments,
                khs = uiState.khsDocuments,
                transkrip = uiState.transkripDocuments,
                currentSemester = uiState.user?.currentSemester ?: 1,
                onUploadNewClick = onUploadNewClick,
                onDocumentClick = onDocumentClick,
                onEditClick = onEditDocument,
                onDeleteClick = onDeleteDocument,
            )
            1 -> BimbinganRiwayatSection(bimbinganHistory = uiState.bimbinganHistory)
            2 -> ChatbotRiwayatSection()
        }
    }
}