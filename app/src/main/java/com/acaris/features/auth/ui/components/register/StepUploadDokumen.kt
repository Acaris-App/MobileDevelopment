package com.acaris.features.auth.ui.components.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.core.utils.FileUtils
import java.io.File

@Composable
fun StepUploadDokumen(
    semester: Int,
    isLoading: Boolean,
    onUploadFile: (String, File, Int?, () -> Unit) -> Unit,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeDocType by rememberSaveable { mutableStateOf("") }
    var activeDocSemester by rememberSaveable { mutableStateOf<Int?>(null) }
    var uploadedDocs by rememberSaveable { mutableStateOf(setOf<String>()) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    val requiredDocs = remember(semester) {
        val list = mutableListOf("transkrip")
        for (i in 1 until semester) {
            list.add("khs_$i")
        }
        list.add("krs_$semester")
        list
    }

    val isAllUploaded = uploadedDocs.containsAll(requiredDocs)

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
                    Text("File berhasil diunggah.")
                }
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null && activeDocType.isNotEmpty()) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                val docKey = if (activeDocSemester != null) "${activeDocType}_$activeDocSemester" else activeDocType

                // 🌟 UPDATE: Lempar activeDocSemester ke ViewModel!
                onUploadFile(activeDocType, file, activeDocSemester) {
                    uploadedDocs = uploadedDocs + docKey
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

        SectionHeader("Unggah Dokumen", "Wajib unggah semua dokumen akademik. Maksimal ukuran per file adalah 1 MB.")

        DocumentSection(
            title = "Transkrip Nilai (Keseluruhan)",
            isUploaded = uploadedDocs.contains("transkrip"),
            onUploadClick = {
                activeDocType = "transkrip"
                activeDocSemester = null
                launcher.launch("application/pdf")
            },
            onRemoveClick = { uploadedDocs = uploadedDocs - "transkrip" }
        )

        if (semester > 1) {
            for (i in 1 until semester) {
                DocumentSection(
                    title = "KHS Semester $i",
                    isUploaded = uploadedDocs.contains("khs_$i"),
                    onUploadClick = {
                        activeDocType = "khs"
                        activeDocSemester = i
                        launcher.launch("application/pdf")
                    },
                    onRemoveClick = { uploadedDocs = uploadedDocs - "khs_$i" }
                )
            }
        }

        DocumentSection(
            title = "KRS Semester $semester (Saat Ini)",
            isUploaded = uploadedDocs.contains("krs_$semester"),
            onUploadClick = {
                activeDocType = "krs"
                activeDocSemester = semester
                launcher.launch("application/pdf")
            },
            onRemoveClick = { uploadedDocs = uploadedDocs - "krs_$semester" }
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomPrimaryButton(
            text = "Kirim & Selesaikan",
            onClick = onFinish,
            enabled = isAllUploaded && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun DocumentSection(
    title: String,
    isUploaded: Boolean,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 8.dp))

        if (isUploaded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("File Siap", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Color.Red)
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