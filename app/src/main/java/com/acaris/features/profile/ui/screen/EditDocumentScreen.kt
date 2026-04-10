package com.acaris.features.profile.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.DottedUploadBox
import com.acaris.core.utils.FileUtils
import com.acaris.features.documents_mahasiswa.presentation.viewmodel.DocumentViewModel
import com.acaris.features.documents_mahasiswa.ui.components.DocumentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDocumentScreen(
    onNavigateBack: () -> Unit,
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    val state by documentViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // 🌟 FIX 3: Trigger load dokumen saat layar Edit dibuka
    LaunchedEffect(Unit) {
        documentViewModel.loadDocuments()
    }

    // State untuk Dialog Pilih Jenis Dokumen
    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("krs") }
    var inputSemester by remember { mutableStateOf("") }

    // Launcher untuk memilih file PDF
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                val semesterInt = inputSemester.toIntOrNull()
                documentViewModel.uploadDocument(
                    type = selectedType,
                    semester = if (selectedType == "transkrip") null else semesterInt,
                    file = file
                )
            }
        }
    }

    // Dialog untuk menentukan jenis dokumen sebelum upload
    if (showUploadDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showUploadDialog = false },
            confirmText = "Pilih File",
            dismissText = "Batal",
            onConfirm = {
                showUploadDialog = false
                launcher.launch("application/pdf")
            },
            onDismiss = { showUploadDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Detail Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown / Pilihan Jenis Dokumen (Sederhana pakai Segmented/Radio)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(selected = selectedType == "krs", onClick = { selectedType = "krs" }, label = { Text("KRS") })
                        FilterChip(selected = selectedType == "khs", onClick = { selectedType = "khs" }, label = { Text("KHS") })
                        FilterChip(selected = selectedType == "transkrip", onClick = { selectedType = "transkrip" }, label = { Text("Transkrip") })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedType != "transkrip") {
                        OutlinedTextField(
                            value = inputSemester,
                            onValueChange = { inputSemester = it },
                            label = { Text("Semester Berapa?") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {}, // 🌟 FIX 5: Logo dan judul dihapus di sub-screen
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
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
                // Judul & Keterangan Pengganti Garis
                Text(
                    text = "Edit Dokumen",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kelola dokumen akademik Anda seperti KRS, KHS, dan Transkrip Nilai.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Area Tambah Dokumen (Memakai Komponen Global)
                Text("Unggah Dokumen Baru", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                DottedUploadBox(
                    text = "Pilih File PDF Maks. 1 MB",
                    onClick = {
                        selectedType = "krs" // Reset form
                        inputSemester = ""
                        showUploadDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Area List Dokumen
                Text("Dokumen Tersimpan", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

                if (state.documents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada dokumen yang diunggah.", color = Color.Gray)
                    }
                } else {
                    state.documents.forEach { doc ->
                        DocumentCard(
                            document = doc,
                            onClick = {
                                // 🌟 FIX 4: Menjalankan fungsi update (buka form), bukan buka PDF
                                selectedType = doc.type.lowercase()
                                inputSemester = doc.semester?.toString() ?: ""
                                showUploadDialog = true
                            },
                            onDeleteClick = { documentId ->
                                documentViewModel.deleteDocument(documentId)
                            },
                            showDelete = true, // 🌟 Nyalakan tong sampah di mode edit
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (state.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}