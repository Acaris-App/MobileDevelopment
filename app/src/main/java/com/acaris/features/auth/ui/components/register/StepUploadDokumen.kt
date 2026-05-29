package com.acaris.features.auth.ui.components.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.utils.FileUtils
// 🌟 IMPORT SHARED COMPONENT
import com.acaris.features.documents_mahasiswa.presentation.model.SharedDocumentUiModel
import com.acaris.features.documents_mahasiswa.ui.components.SharedDocumentManager
import java.io.File

data class UploadedDocInfo(val fileName: String, val documentId: String)

@Composable
fun StepUploadDokumen(
    semester: Int,
    isLoading: Boolean,
    onUploadFile: (String, File, Int?, String?, (String) -> Unit) -> Unit,
    onDeleteFile: (String, () -> Unit) -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeDocType by rememberSaveable { mutableStateOf("") }
    var activeDocSemester by rememberSaveable { mutableStateOf<Int?>(null) }

    var uploadedDocs by remember { mutableStateOf(mapOf<String, UploadedDocInfo>()) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by rememberSaveable { mutableStateOf(false) }

    val requiredDocs = remember(semester) {
        val list = mutableListOf("transkrip")
        for (i in 1 until semester) {
            list.add("khs_$i")
            list.add("krs_$i")
        }
        list
    }

    val isAllUploaded = requiredDocs.all { uploadedDocs.containsKey(it) }

    if (showSuccessDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showSuccessDialog = false },
            confirmText = "Oke",
            onConfirm = { showSuccessDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dokumen berhasil disimpan.")
                }
            }
        )
    }

    if (showWarningDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showWarningDialog = false },
            confirmText = "Tetap Lanjutkan",
            onConfirm = {
                showWarningDialog = false
                onFinish()
            },
            dismissText = "Lengkapi Dokumen",
            onDismiss = { showWarningDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Peringatan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Anda belum melengkapi semua dokumen akademik. Anda dapat melewati langkah ini, namun Anda harus melengkapi dokumen di menu profil nantinya agar fitur lain terbuka.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && activeDocType.isNotEmpty()) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                val docKey = if (activeDocSemester != null) "${activeDocType}_$activeDocSemester" else activeDocType

                val existingDocId = uploadedDocs[docKey]?.documentId

                onUploadFile(activeDocType, file, activeDocSemester, existingDocId) { newDocId ->
                    uploadedDocs = uploadedDocs + (docKey to UploadedDocInfo(file.name, newDocId))
                    showSuccessDialog = true
                }
            }
        }
    }

    // 🌟 MAPPING STATE LOKAL KE UI MODEL NETRAL
    val mappedDocuments = uploadedDocs.map { (key, info) ->
        val parts = key.split("_")
        val type = parts[0]
        val sem = if (parts.size > 1) parts[1].toIntOrNull() else null

        SharedDocumentUiModel(
            id = info.documentId,
            type = type,
            semester = sem,
            fileUrl = "",
            uploadedAt = info.fileName
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Unggah Dokumen", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Maksimal ukuran per file adalah 1 MB. Dapat dilewati jika dokumen belum siap.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🌟 PANGGIL SHARED COMPONENT DI SINI
        SharedDocumentManager(
            documents = mappedDocuments,
            currentSemester = semester,
            isReadOnly = false,
            onViewDocument = {
                // Beri tahu user kalau file belum bisa dilihat selama proses registrasi
                android.widget.Toast.makeText(context, "Pratinjau dokumen tersedia setelah pendaftaran selesai.", android.widget.Toast.LENGTH_SHORT).show()
            },
            onUploadDocument = { type, sem, _ ->
                activeDocType = type
                activeDocSemester = sem
                launcher.launch("application/pdf")
            },
            onDeleteDocument = { docId ->
                // Cari key (misal: "krs_2") berdasarkan ID dokumen
                val entry = uploadedDocs.entries.find { it.value.documentId == docId }
                if (entry != null) {
                    onDeleteFile(docId) {
                        // Hapus dari state lokal jika berhasil dihapus di server
                        uploadedDocs = uploadedDocs - entry.key
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomPrimaryButton(
            text = "Selesaikan Pendaftaran",
            onClick = {
                if (isAllUploaded) {
                    onFinish()
                } else {
                    showWarningDialog = true
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }

    CustomLoadingOverlay(isLoading = isLoading)
}