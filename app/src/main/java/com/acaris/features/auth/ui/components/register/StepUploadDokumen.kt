package com.acaris.features.auth.ui.components.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.core.utils.FileUtils
import java.io.File

data class UploadedDocInfo(val fileName: String, val documentId: Int)

@Composable
fun StepUploadDokumen(
    semester: Int,
    isLoading: Boolean,
    onUploadFile: (String, File, Int?, Int?, (Int) -> Unit) -> Unit,
    onDeleteFile: (Int, () -> Unit) -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeDocType by rememberSaveable { mutableStateOf("") }
    var activeDocSemester by rememberSaveable { mutableStateOf<Int?>(null) }

    var uploadedDocs by remember { mutableStateOf(mapOf<String, UploadedDocInfo>()) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by rememberSaveable { mutableStateOf(false) }

    var showOrderErrorDialog by rememberSaveable { mutableStateOf(false) }
    var orderErrorMessage by rememberSaveable { mutableStateOf("") }

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

    if (showOrderErrorDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showOrderErrorDialog = false },
            confirmText = "Mengerti",
            onConfirm = { showOrderErrorDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Aksi Ditolak",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = orderErrorMessage,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Unggah Dokumen", "Maksimal ukuran per file adalah 1 MB. Dapat dilewati jika dokumen belum siap.")

        DocumentSection(
            title = "Transkrip Nilai (Keseluruhan)",
            fileName = uploadedDocs["transkrip"]?.fileName,
            onUploadClick = {
                activeDocType = "transkrip"
                activeDocSemester = null
                launcher.launch("application/pdf")
            },
            onDeleteClick = {
                uploadedDocs["transkrip"]?.documentId?.let { docId ->
                    onDeleteFile(docId) { uploadedDocs = uploadedDocs - "transkrip" }
                }
            }
        )

        if (semester > 1) {
            for (i in 1 until semester) {
                DocumentSection(
                    title = "KRS Semester $i",
                    fileName = uploadedDocs["krs_$i"]?.fileName,
                    onUploadClick = {
                        // Kunci Urutan KRS
                        if (i == 1 || uploadedDocs.containsKey("krs_${i - 1}")) {
                            activeDocType = "krs"
                            activeDocSemester = i
                            launcher.launch("application/pdf")
                        } else {
                            orderErrorMessage = "Upload dokumen KRS harus berurutan. Silakan unggah KRS Semester ${i - 1} terlebih dahulu."
                            showOrderErrorDialog = true
                        }
                    },
                    onDeleteClick = {
                        uploadedDocs["krs_$i"]?.documentId?.let { docId ->
                            onDeleteFile(docId) { uploadedDocs = uploadedDocs - "krs_$i" }
                        }
                    }
                )

                DocumentSection(
                    title = "KHS Semester $i",
                    fileName = uploadedDocs["khs_$i"]?.fileName,
                    onUploadClick = {
                        // Kunci Urutan KHS
                        if (i == 1 || uploadedDocs.containsKey("khs_${i - 1}")) {
                            activeDocType = "khs"
                            activeDocSemester = i
                            launcher.launch("application/pdf")
                        } else {
                            orderErrorMessage = "Upload dokumen KHS harus berurutan. Silakan unggah KHS Semester ${i - 1} terlebih dahulu."
                            showOrderErrorDialog = true
                        }
                    },
                    onDeleteClick = {
                        uploadedDocs["khs_$i"]?.documentId?.let { docId ->
                            onDeleteFile(docId) { uploadedDocs = uploadedDocs - "khs_$i" }
                        }
                    }
                )
            }
        }

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

@Composable
fun DocumentSection(
    title: String,
    fileName: String?,
    onUploadClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

        if (fileName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .clickable { onUploadClick() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = fileName,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus Dokumen", tint = Color.Red)
                }
            }
        } else {
            DottedUploadBox(
                text = "Pilih File PDF",
                onClick = onUploadClick
            )
            Text(
                text = "Maksimal 1 MB. Format: .pdf",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}