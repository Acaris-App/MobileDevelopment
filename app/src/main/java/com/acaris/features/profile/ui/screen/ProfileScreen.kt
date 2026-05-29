package com.acaris.features.profile.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.utils.FileUtils
import com.acaris.features.documents_mahasiswa.presentation.viewmodel.DocumentViewModel
import com.acaris.features.documents_mahasiswa.ui.components.SharedDocumentManager
import com.acaris.features.profile.presentation.viewmodel.ProfileViewModel
import com.acaris.features.profile.ui.components.ProfileInfoCard

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditDataDiri: () -> Unit,
    // onNavigateToEditDokumen dihapus karena sudah tidak pindah halaman!
    onNavigateToChangePassword: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val documentState by documentViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Data Diri", "Dokumen Akademik")

    // --- STATE UNTUK EDIT DOKUMEN ---
    var pendingUploadType by remember { mutableStateOf("") }
    var pendingUploadSemester by remember { mutableStateOf<Int?>(null) }
    var documentIdToUpdate by remember { mutableStateOf<String?>(null) }
    var showReplaceDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileViewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isMahasiswa = profileState.userProfile?.role == "mahasiswa"
    LaunchedEffect(isMahasiswa) {
        if (isMahasiswa) {
            documentViewModel.loadDocuments()
        }
    }

    // --- LAUNCHER PEMILIHAN FILE ---
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.uriToFile(context, uri)
            if (file != null) {
                if (documentIdToUpdate != null) {
                    documentViewModel.updateDocument(
                        documentId = documentIdToUpdate!!,
                        semester = pendingUploadSemester,
                        file = file
                    )
                } else if (pendingUploadType.isNotEmpty()) {
                    documentViewModel.uploadDocument(
                        type = pendingUploadType,
                        semester = pendingUploadSemester,
                        file = file
                    )
                }
            }
        }
        documentIdToUpdate = null
        pendingUploadType = ""
    }

    // --- DIALOG BOXES (Ganti, Hapus, Sukses, Error) ---
    if (showReplaceDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                showReplaceDialog = false
                documentIdToUpdate = null
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
                documentIdToUpdate = null
            }
        )
    }

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

    if (documentState.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { documentViewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = documentState.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "OK",
            onConfirm = { documentViewModel.clearMessages() }
        )
    }

    if (documentState.errorMessage != null) {
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
                        text = documentState.errorMessage ?: "",
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

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isMahasiswa) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    when (selectedTabIndex) {
                        0 -> {
                            // TAB 1: DATA DIRI
                            profileState.userProfile?.let { user ->
                                ProfileInfoCard(
                                    userProfile = user,
                                    onEditClick = onNavigateToEditDataDiri,
                                    onChangePasswordClick = onNavigateToChangePassword
                                )
                            }
                        }
                        1 -> {
                            // TAB 2: DOKUMEN (Gunakan Shared Component)
                            SharedDocumentManager(
                                documents = documentState.documents,
                                currentSemester = profileState.userProfile?.currentSemester ?: 1,
                                isReadOnly = false, // Mahasiswa bisa edit dokumen miliknya
                                onViewDocument = { url ->
                                    if (url.isNotBlank()) uriHandler.openUri(url)
                                },
                                onUploadDocument = { type, sem, existingDocId ->
                                    pendingUploadType = type
                                    pendingUploadSemester = sem
                                    if (existingDocId != null) {
                                        documentIdToUpdate = existingDocId
                                        showReplaceDialog = true
                                    } else {
                                        launcher.launch("application/pdf")
                                    }
                                },
                                onDeleteDocument = { id ->
                                    documentIdToDelete = id
                                }
                            )
                        }
                    }
                } else {
                    // JIKA BUKAN MAHASISWA, TAMPILKAN LANGSUNG PROFIL (TANPA TAB)
                    profileState.userProfile?.let { user ->
                        ProfileInfoCard(
                            userProfile = user,
                            onEditClick = onNavigateToEditDataDiri,
                            onChangePasswordClick = onNavigateToChangePassword
                        )
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }

            if (profileState.isLoading || documentState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}