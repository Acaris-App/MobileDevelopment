package com.acaris.features.user_management.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.utils.FileUtils
import com.acaris.features.user_management.presentation.model.MahasiswaDocumentUiModel
import com.acaris.features.user_management.presentation.viewmodel.UserDetailViewModel
import com.acaris.features.user_management.ui.components.MahasiswaTabSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var pendingUploadType by remember { mutableStateOf("") }
    var pendingUploadSemester by remember { mutableStateOf<Int?>(null) }
    var documentIdToUpdate by remember { mutableStateOf<String?>(null) }

    var showReplaceDialog by remember { mutableStateOf(false) }
    var documentToDelete by remember { mutableStateOf<MahasiswaDocumentUiModel?>(null) }

    var showOrderErrorDialog by remember { mutableStateOf(false) }
    var orderErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.loadUserDetail(userId)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                if (documentIdToUpdate != null) {
                    viewModel.updateDocument(userId = userId, documentId = documentIdToUpdate!!, semester = pendingUploadSemester, file = file)
                } else if (pendingUploadType.isNotEmpty()) {
                    viewModel.uploadDocument(userId = userId, documentType = pendingUploadType, semester = pendingUploadSemester, file = file)
                }
            }
        }
        documentIdToUpdate = null
        pendingUploadType = ""
        pendingUploadSemester = null
    }

    if (showReplaceDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showReplaceDialog = false; documentIdToUpdate = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ganti Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Apakah Anda yakin ingin mengganti dokumen ini dengan file PDF yang baru?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Pilih File Baru",
            onConfirm = { showReplaceDialog = false; launcher.launch("application/pdf") },
            dismissText = "Batal",
            onDismiss = { showReplaceDialog = false; documentIdToUpdate = null }
        )
    }

    if (showOrderErrorDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showOrderErrorDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Aksi Ditolak", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = orderErrorMessage, textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "Mengerti",
            onConfirm = { showOrderErrorDialog = false }
        )
    }

    if (documentToDelete != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { documentToDelete = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hapus Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tindakan ini tidak dapat dibatalkan. Yakin ingin menghapus dokumen ini?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Hapus",
            onConfirm = { documentToDelete?.let { viewModel.deleteDocument(userId, it.id) }; documentToDelete = null },
            dismissText = "Batal",
            onDismiss = { documentToDelete = null }
        )
    }

    if (uiState.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "OK",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    if (uiState.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gagal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.errorMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "Tutup",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Akademik") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") } })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp))
            } else {
                uiState.user?.let { user ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                                if (!user.profilePictureUrl.isNullOrEmpty()) {
                                    AsyncImage(model = user.profilePictureUrl, contentDescription = "Foto Profil", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                } else {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(text = user.identifier, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        HorizontalDivider()

                        if (user.role.lowercase() == "mahasiswa") {
                            MahasiswaTabSection(
                                uiState = uiState,
                                onUploadNewClick = { type, semester ->
                                    android.util.Log.d("DEBUG_UPLOAD", "type=$type, semester=$semester")
                                    pendingUploadType = type
                                    pendingUploadSemester = semester
                                    launcher.launch("application/pdf")
                                },
                                onDocumentClick = { doc ->
                                    if (!doc.filePath.isNullOrEmpty()) uriHandler.openUri(doc.filePath)
                                },
                                onEditDocument = { doc ->
                                    documentIdToUpdate = doc.id
                                    pendingUploadSemester = doc.semester
                                    showReplaceDialog = true
                                },
                                onDeleteDocument = { doc -> documentToDelete = doc },
                                onOrderError = { msg ->
                                    orderErrorMessage = msg
                                    showOrderErrorDialog = true
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Detail dokumen dan bimbingan tidak tersedia untuk peran ${user.role}.") }
                        }
                    }
                }
            }
        }
    }
}