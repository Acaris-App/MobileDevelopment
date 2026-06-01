package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomChipTabRow // 🌟 IMPORT CUSTOM CHIP TAB ROW
import com.acaris.features.documents_mahasiswa.presentation.model.SharedDocumentUiModel
import com.acaris.features.documents_mahasiswa.ui.components.SharedDocumentManager
import com.acaris.features.user_management.presentation.model.UserDetailUiState

@Composable
fun MahasiswaTabSection(
    uiState: UserDetailUiState,
    onViewDocument: (String) -> Unit,
    onUploadOrEditDocument: (type: String, semester: Int?, existingDocId: String?) -> Unit,
    onDeleteDocument: (String) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dokumen", "Bimbingan", "Chatbot (Aca)")

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // 🌟 FIX 1: Terapkan Custom Tab
        CustomChipTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> {
                // 1. Gabungkan semua dokumen Admin
                val allAdminDocs = uiState.krsDocuments + uiState.khsDocuments + uiState.transkripDocuments

                // 2. Map ke UI Model Netral
                val mappedDocuments = allAdminDocs.map { doc ->
                    SharedDocumentUiModel(
                        id = doc.id,
                        type = doc.documentType,
                        semester = doc.semester,
                        fileUrl = doc.filePath ?: "",
                        uploadedAt = doc.uploadedAt ?: "-"
                    )
                }

                // BUNGKUS DENGAN SCROLL DAN PADDING DI SINI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    // 3. Panggil Shared Component
                    SharedDocumentManager(
                        documents = mappedDocuments,
                        currentSemester = uiState.user?.currentSemester ?: 1,
                        isReadOnly = false, // Admin bisa edit & hapus
                        onViewDocument = onViewDocument,
                        onUploadDocument = onUploadOrEditDocument,
                        onDeleteDocument = onDeleteDocument
                    )

                    // Spacer tambahan di bawah agar dokumen terakhir tidak tertutup navigasi
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            1 -> BimbinganRiwayatSection(bimbinganHistory = uiState.bimbinganHistory)
            2 -> ChatbotRiwayatSection()
        }
    }
}