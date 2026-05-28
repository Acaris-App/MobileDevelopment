package com.acaris.features.profile.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.core.utils.FileUtils
import com.acaris.features.documents_mahasiswa.presentation.viewmodel.DocumentViewModel
import com.acaris.features.documents_mahasiswa.ui.components.DocumentCard
import com.acaris.features.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDocumentScreen(
    onNavigateBack: () -> Unit,
    documentViewModel: DocumentViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val state by documentViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        documentViewModel.loadDocuments()
    }

    // State untuk upload dokumen baru
    var pendingUploadType by remember { mutableStateOf("") }
    var pendingUploadSemester by remember { mutableStateOf<Int?>(null) }

    // State baru untuk menyimpan ID dokumen yang mau di-update
    var documentIdToUpdate by remember { mutableStateOf<String?>(null) }

    var showReplaceDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }

    // 🌟 REVISI SPRINT 1: State showOrderErrorDialog dan orderErrorMessage sudah dihapus!

    // Logika Launcher dipisah jadi dua (Update vs Upload)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                if (documentIdToUpdate != null) {
                    // Jalur PUT: Ganti dokumen yang sudah ada
                    documentViewModel.updateDocument(
                        documentId = documentIdToUpdate!!,
                        semester = pendingUploadSemester,
                        file = file
                    )
                } else if (pendingUploadType.isNotEmpty()) {
                    // Jalur POST: Tambah dokumen baru
                    documentViewModel.uploadDocument(
                        type = pendingUploadType,
                        semester = pendingUploadSemester,
                        file = file
                    )
                }
            }
        }
        // Reset state setelah file dipilih atau dibatalkan
        documentIdToUpdate = null
        pendingUploadType = ""
    }

    if (showReplaceDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                showReplaceDialog = false
                documentIdToUpdate = null // Reset jika batal
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ganti Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dokumen ini sudah ada. Apakah Anda yakin ingin menggantinya dengan file PDF yang baru?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Pilih File",
            onConfirm = {
                showReplaceDialog = false
                launcher.launch("application/pdf")
            },
            dismissText = "Batal",
            onDismiss = {
                showReplaceDialog = false
                documentIdToUpdate = null // Reset jika batal
            }
        )
    }

    // 🌟 REVISI SPRINT 1: CustomDialog untuk showOrderErrorDialog sudah dicabut dari sini!

    if (documentIdToDelete != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { documentIdToDelete = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hapus Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tindakan ini tidak dapat dibatalkan. Yakin ingin menghapus dokumen ini dari sistem?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Hapus",
            onConfirm = {
                documentIdToDelete?.let { documentViewModel.deleteDocument(it) }
                documentIdToDelete = null
            },
            dismissText = "Batal",
            onDismiss = { documentIdToDelete = null }
        )
    }

    if (state.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                documentViewModel.clearMessages()
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "OK",
            onConfirm = {
                documentViewModel.clearMessages()
            }
        )
    }

    if (state.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { documentViewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Gagal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            },
            confirmText = "Tutup",
            onConfirm = { documentViewModel.clearMessages() }
        )
    }

    val currentSemester = profileState.userProfile?.currentSemester ?: 1
    val requiredSemesters = if (currentSemester > 1) (1 until currentSemester).toList() else emptyList()

    val krsSemesters = (requiredSemesters + state.documents.filter { it.type.lowercase() == "krs" }.mapNotNull { it.semester }).distinct().sorted()
    val khsSemesters = (requiredSemesters + state.documents.filter { it.type.lowercase() == "khs" }.mapNotNull { it.semester }).distinct().sorted()

    val getDoc = { type: String, semester: Int? ->
        state.documents.find { doc ->
            if (type.lowercase() == "transkrip") doc.type.lowercase() == "transkrip"
            else doc.type.lowercase() == type.lowercase() && doc.semester == semester
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    CustomBackButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(text = "Kelola Dokumen", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Lengkapi daftar dokumen akademik Anda berdasarkan semester saat ini.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(32.dp))

                // CONTAINER 1: TRANSKRIP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Transkrip Nilai Terakhir", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))

                        val transkripDoc = getDoc("transkrip", null)
                        if (transkripDoc != null) {
                            DocumentCard(
                                document = transkripDoc,
                                onClick = {
                                    documentIdToUpdate = transkripDoc.id
                                    pendingUploadSemester = null
                                    showReplaceDialog = true
                                },
                                onDeleteClick = { documentIdToDelete = it },
                                showDelete = true
                            )
                        } else {
                            DottedUploadBox(
                                text = "Unggah Transkrip PDF (Maks 1MB)",
                                onClick = {
                                    pendingUploadType = "transkrip"
                                    pendingUploadSemester = null
                                    launcher.launch("application/pdf")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CONTAINER 2: KRS
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
                                val krsDoc = getDoc("krs", sem)

                                // 🌟 REVISI SPRINT 1: Hapus pengecekan urutan, langsung eksekusi
                                val onClickAction = {
                                    pendingUploadSemester = sem
                                    if (krsDoc != null) {
                                        documentIdToUpdate = krsDoc.id
                                        showReplaceDialog = true
                                    } else {
                                        pendingUploadType = "krs"
                                        launcher.launch("application/pdf")
                                    }
                                }

                                if (krsDoc != null) {
                                    DocumentCard(
                                        document = krsDoc,
                                        onClick = onClickAction,
                                        onDeleteClick = { documentIdToDelete = it },
                                        showDelete = true
                                    )
                                } else {
                                    DottedUploadBox(
                                        text = "Unggah KRS Semester $sem",
                                        onClick = onClickAction
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CONTAINER 3: KHS
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
                                val khsDoc = getDoc("khs", sem)

                                // 🌟 REVISI SPRINT 1: Hapus pengecekan urutan, langsung eksekusi
                                val onClickAction = {
                                    pendingUploadSemester = sem
                                    if (khsDoc != null) {
                                        documentIdToUpdate = khsDoc.id
                                        showReplaceDialog = true
                                    } else {
                                        pendingUploadType = "khs"
                                        launcher.launch("application/pdf")
                                    }
                                }

                                if (khsDoc != null) {
                                    DocumentCard(
                                        document = khsDoc,
                                        onClick = onClickAction,
                                        onDeleteClick = { documentIdToDelete = it },
                                        showDelete = true
                                    )
                                } else {
                                    DottedUploadBox(
                                        text = "Unggah KHS Semester $sem",
                                        onClick = onClickAction
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }

            if (state.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}