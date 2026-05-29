package com.acaris.features.documents_mahasiswa.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.features.documents_mahasiswa.presentation.model.SharedDocumentUiModel

@Composable
fun SharedDocumentManager(
    documents: List<SharedDocumentUiModel>,
    currentSemester: Int,
    isReadOnly: Boolean = false,
    onViewDocument: (url: String) -> Unit,
    onUploadDocument: (type: String, semester: Int?, existingDocId: String?) -> Unit = { _, _, _ -> },
    onDeleteDocument: (documentId: String) -> Unit = {}
) {
    val requiredSemesters = if (currentSemester > 1) (1 until currentSemester).toList() else emptyList()
    val krsSemesters = (requiredSemesters + documents.filter { it.type.lowercase() == "krs" }.mapNotNull { it.semester }).distinct().sorted()
    val khsSemesters = (requiredSemesters + documents.filter { it.type.lowercase() == "khs" }.mapNotNull { it.semester }).distinct().sorted()

    val transkripDoc = documents.find { it.type.lowercase() == "transkrip" }
    val getDoc = { type: String, sem: Int -> documents.find { it.type.lowercase() == type && it.semester == sem } }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- TRANSKRIP ---
        DocumentSectionCard(title = "Transkrip Nilai Terakhir") {
            if (transkripDoc != null) {
                SharedDocumentCard(
                    document = transkripDoc,
                    isReadOnly = isReadOnly,
                    onClick = { onViewDocument(transkripDoc.fileUrl) },
                    onEditClick = { onUploadDocument("transkrip", null, transkripDoc.id) },
                    onDeleteClick = { onDeleteDocument(transkripDoc.id) }
                )
            } else {
                EmptyDocumentSlot(
                    isReadOnly = isReadOnly,
                    text = "Unggah Transkrip PDF",
                    onClick = { onUploadDocument("transkrip", null, null) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- KRS ---
        DocumentSectionCard(title = "Kartu Rencana Studi (KRS)") {
            if (krsSemesters.isEmpty()) {
                Text("Belum ada dokumen yang diunggah.", color = Color.Gray)
            } else {
                krsSemesters.forEachIndexed { index, sem ->
                    val doc = getDoc("krs", sem)
                    if (doc != null) {
                        SharedDocumentCard(
                            document = doc,
                            isReadOnly = isReadOnly,
                            onClick = { onViewDocument(doc.fileUrl) },
                            onEditClick = { onUploadDocument("krs", sem, doc.id) },
                            onDeleteClick = { onDeleteDocument(doc.id) }
                        )
                    } else {
                        EmptyDocumentSlot(
                            isReadOnly = isReadOnly,
                            text = "Unggah KRS Semester $sem",
                            onClick = { onUploadDocument("krs", sem, null) }
                        )
                    }
                    if (index < krsSemesters.size - 1) Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- KHS ---
        DocumentSectionCard(title = "Kartu Hasil Studi (KHS)") {
            if (khsSemesters.isEmpty()) {
                Text("Belum ada dokumen yang diunggah.", color = Color.Gray)
            } else {
                khsSemesters.forEachIndexed { index, sem ->
                    val doc = getDoc("khs", sem)
                    if (doc != null) {
                        SharedDocumentCard(
                            document = doc,
                            isReadOnly = isReadOnly,
                            onClick = { onViewDocument(doc.fileUrl) },
                            onEditClick = { onUploadDocument("khs", sem, doc.id) },
                            onDeleteClick = { onDeleteDocument(doc.id) }
                        )
                    } else {
                        EmptyDocumentSlot(
                            isReadOnly = isReadOnly,
                            text = "Unggah KHS Semester $sem",
                            onClick = { onUploadDocument("khs", sem, null) }
                        )
                    }
                    if (index < khsSemesters.size - 1) Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Sub-komponen pembantu
@Composable
private fun EmptyDocumentSlot(isReadOnly: Boolean, text: String, onClick: () -> Unit) {
    if (isReadOnly) {
        Text("Dokumen belum diunggah", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    } else {
        DottedUploadBox(text = text, onClick = onClick)
    }
}

@Composable
private fun DocumentSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}