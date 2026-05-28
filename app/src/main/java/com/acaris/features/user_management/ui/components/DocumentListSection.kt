package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.features.user_management.presentation.model.MahasiswaDocumentUiModel

val MahasiswaDocumentUiModel.displayTitle: String
    get() = if (this.semester != null && this.semester > 0) {
        "${this.documentType.uppercase()} Semester ${this.semester}"
    } else {
        this.documentType.uppercase()
    }

@Composable
fun DocumentListSection(
    krs: List<MahasiswaDocumentUiModel>,
    khs: List<MahasiswaDocumentUiModel>,
    transkrip: List<MahasiswaDocumentUiModel>,
    currentSemester: Int,
    onUploadNewClick: (type: String, semester: Int?) -> Unit,
    onDocumentClick: (MahasiswaDocumentUiModel) -> Unit,
    onEditClick: (MahasiswaDocumentUiModel) -> Unit,
    onDeleteClick: (MahasiswaDocumentUiModel) -> Unit
) {
    val requiredSemesters = if (currentSemester > 1) (1 until currentSemester).toList() else emptyList()
    val krsSemesters = (requiredSemesters + krs.mapNotNull { it.semester }).distinct().sorted()
    val khsSemesters = (requiredSemesters + khs.mapNotNull { it.semester }).distinct().sorted()

    val getKrs = { sem: Int -> krs.find { it.semester == sem } }
    val getKhs = { sem: Int -> khs.find { it.semester == sem } }
    val transkripDoc = transkrip.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Transkrip Nilai Terakhir", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (transkripDoc != null) {
                        AdminDocumentCard(transkripDoc, onDocumentClick, onEditClick, onDeleteClick)
                    } else {
                        DottedUploadBox(
                            text = "Unggah Transkrip PDF (Maks 1MB)",
                            onClick = { onUploadNewClick("transkrip", 0) }
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Kartu Rencana Studi (KRS)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (krsSemesters.isEmpty()) {
                        Text("Belum ada KRS yang perlu diunggah.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    } else {
                        krsSemesters.forEachIndexed { index, sem ->
                            val krsDoc = getKrs(sem)

                            // 🌟 REVISI SPRINT 1: Hapus pengecekan urutan
                            val onClickAction = {
                                onUploadNewClick("krs", sem)
                            }

                            if (krsDoc != null) {
                                AdminDocumentCard(krsDoc, onDocumentClick, onEditClick, onDeleteClick)
                            } else {
                                DottedUploadBox(text = "Unggah KRS Semester $sem", onClick = onClickAction)
                            }
                            if (index < krsSemesters.size - 1) Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Kartu Hasil Studi (KHS)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (khsSemesters.isEmpty()) {
                        Text("Belum ada KHS yang perlu diunggah.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    } else {
                        khsSemesters.forEachIndexed { index, sem ->
                            val khsDoc = getKhs(sem)

                            // 🌟 REVISI SPRINT 1: Hapus pengecekan urutan
                            val onClickAction = {
                                onUploadNewClick("khs", sem)
                            }

                            if (khsDoc != null) {
                                AdminDocumentCard(khsDoc, onDocumentClick, onEditClick, onDeleteClick)
                            } else {
                                DottedUploadBox(text = "Unggah KHS Semester $sem", onClick = onClickAction)
                            }
                            if (index < khsSemesters.size - 1) Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDocumentCard(
    document: MahasiswaDocumentUiModel,
    onDocumentClick: (MahasiswaDocumentUiModel) -> Unit,
    onEditClick: (MahasiswaDocumentUiModel) -> Unit,
    onDeleteClick: (MahasiswaDocumentUiModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp), clip = false).clip(RoundedCornerShape(16.dp)).clickable { onDocumentClick(document) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = document.displayTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "Diunggah: ${document.uploadedAt}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row {
                CustomCircularIconButton(icon = Icons.Outlined.Edit, contentDescription = "Edit", color = Color.Gray, onClick = { onEditClick(document) })
                Spacer(modifier = Modifier.width(8.dp))
                CustomCircularIconButton(icon = Icons.Outlined.Delete, contentDescription = "Hapus", color = MaterialTheme.colorScheme.error, onClick = { onDeleteClick(document) })
            }
        }
    }
}