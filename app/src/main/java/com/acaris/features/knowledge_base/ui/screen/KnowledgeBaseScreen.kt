package com.acaris.features.knowledge_base.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.utils.FileUtils
import com.acaris.features.knowledge_base.presentation.model.KnowledgeUiModel
import com.acaris.features.knowledge_base.presentation.viewmodel.KnowledgeViewModel
import com.acaris.features.knowledge_base.ui.components.CategoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    viewModel: KnowledgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val categories = listOf("Peraturan Akademik", "Jadwal", "Kurikulum", "Peraturan Rektor", "KKNI")

    var showUploadDialog by remember { mutableStateOf(false) }

    // 🌟 State Baru untuk membedakan Mode Edit atau Tambah Baru
    var isEditMode by remember { mutableStateOf(false) }
    var editDocumentId by remember { mutableStateOf<String?>(null) }

    var uploadTargetCategory by remember { mutableStateOf("") }
    var uploadTitle by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var documentToDelete by remember { mutableStateOf<KnowledgeUiModel?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            selectedFileName = FileUtils.getFileName(context, it)
        }
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
            showUploadDialog = false
            showDeleteDialog = false
        }
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // 🌟 FIX: Judul dihapus, diganti menjadi teks keterangan yang lebih kecil
                    Text(
                        text = "Dokumen knowledge base chatbot Aca",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                items(categories) { category ->
                    val docsInCategory = uiState.documents.filter { it.category == category }

                    CategoryCard(
                        category = category,
                        documents = docsInCategory,
                        onUploadClick = {
                            isEditMode = false
                            editDocumentId = null
                            uploadTargetCategory = category
                            uploadTitle = ""
                            selectedFileUri = null
                            selectedFileName = null
                            showUploadDialog = true
                        },
                        onReadClick = { doc ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.fileUrl))
                            context.startActivity(intent)
                        },
                        onEditClick = { doc ->
                            isEditMode = true
                            editDocumentId = doc.id
                            uploadTargetCategory = doc.category
                            uploadTitle = doc.title
                            selectedFileUri = null
                            selectedFileName = doc.fileName
                            showUploadDialog = true
                        },
                        onDeleteClick = { doc ->
                            documentToDelete = doc
                            showDeleteDialog = true
                        }
                    )
                }
            }

            if (uiState.isLoading || uiState.isUploading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }

    // ==========================================
    // DIALOG UPLOAD & UPDATE
    // ==========================================
    CustomDialog(
        showDialog = showUploadDialog,
        onDismissRequest = { showUploadDialog = false },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditMode) "Edit Dokumen" else "Upload Dokumen Baru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kategori: $uploadTargetCategory",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uploadTitle,
                    onValueChange = { uploadTitle = it },
                    label = { Text("Judul Dokumen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Ganti File PDF" else "Pilih File PDF")
                }

                if (selectedFileName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "File: $selectedFileName",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        confirmText = "Simpan",
        onConfirm = {
            if (uploadTitle.isNotBlank()) {
                if (isEditMode) {
                    val file = if (selectedFileUri != null) {
                        FileUtils.uriToFile(context, selectedFileUri!!, selectedFileName ?: "update_${System.currentTimeMillis()}.pdf")
                    } else null

                    viewModel.updateDocument(editDocumentId!!, uploadTitle, uploadTargetCategory, file)
                } else {
                    if (selectedFileUri != null) {
                        val file = FileUtils.uriToFile(context, selectedFileUri!!, selectedFileName ?: "upload_${System.currentTimeMillis()}.pdf")
                        if (file != null) {
                            viewModel.uploadDocument(uploadTitle, uploadTargetCategory, file)
                        } else {
                            Toast.makeText(context, "Gagal membaca file PDF", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "File PDF belum dipilih!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        },
        dismissText = "Batal",
        onDismiss = { showUploadDialog = false }
    )

    // ==========================================
    // DIALOG HAPUS
    // ==========================================
    CustomDialog(
        showDialog = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Yakin ingin menghapus dokumen '${documentToDelete?.title}'?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Jika dihapus, Anda bisa mengupload dokumen baru di kategori ini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        confirmText = "Hapus",
        onConfirm = {
            documentToDelete?.id?.let { viewModel.deleteDocument(it) }
        },
        dismissText = "Batal",
        onDismiss = { showDeleteDialog = false }
    )
}