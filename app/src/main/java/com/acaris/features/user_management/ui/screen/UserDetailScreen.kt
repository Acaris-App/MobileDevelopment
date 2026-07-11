package com.acaris.features.user_management.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.utils.FileUtils
import com.acaris.features.user_management.presentation.viewmodel.UserDetailViewModel
import com.acaris.features.user_management.ui.components.MahasiswaTabSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToChatbotDetail: (mahasiswaId: String, sessionId: String) -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var pendingUploadType by remember { mutableStateOf("") }
    var pendingUploadSemester by remember { mutableStateOf<Int?>(null) }
    var documentIdToUpdate by remember { mutableStateOf<String?>(null) }

    var showReplaceDialog by remember { mutableStateOf(false) }
    var documentIdToDelete by remember { mutableStateOf<String?>(null) }

    var showZoomedImage by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(userId) {
        viewModel.loadUserDetail(userId)
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
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

    if (documentIdToDelete != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { documentIdToDelete = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hapus Dokumen", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tindakan ini tidak dapat dibatalkan. Yakin ingin menghapus dokumen ini?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Hapus",
            onConfirm = {
                documentIdToDelete?.let { viewModel.deleteDocument(userId, it) }
                documentIdToDelete = null
            },
            dismissText = "Batal",
            onDismiss = { documentIdToDelete = null }
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
            TopAppBar(
                title = {
                    uiState.user?.let { user ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable { showZoomedImage = true }
                            ) {
                                if (!user.profilePictureUrl.isNullOrEmpty()) {
                                    AsyncImage(model = user.profilePictureUrl, contentDescription = "Foto Profil", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                } else {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.Transparent)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                        .padding(horizontal = 10.dp, vertical = 1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.identifier,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    CustomBackButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadUserDetail(userId)
            },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading && !isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.errorMessage != null) {
                    Text("Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                } else {
                    uiState.user?.let { user ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            if (user.role.lowercase() == "mahasiswa") {
                                MahasiswaTabSection(
                                    uiState = uiState,
                                    onViewDocument = { url ->
                                        if (url.isNotBlank()) uriHandler.openUri(url)
                                    },
                                    onUploadOrEditDocument = { type, semester, existingDocId ->
                                        pendingUploadType = type
                                        pendingUploadSemester = semester
                                        if (existingDocId != null) {
                                            documentIdToUpdate = existingDocId
                                            showReplaceDialog = true
                                        } else {
                                            launcher.launch("application/pdf")
                                        }
                                    },
                                    onDeleteDocument = { docId ->
                                        documentIdToDelete = docId
                                    },
                                    onNavigateToChatbotDetail = { sessionId ->
                                        onNavigateToChatbotDetail(user.id, sessionId)
                                    }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Detail dokumen dan bimbingan tidak tersedia untuk peran ${user.role}.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showZoomedImage) {
        CustomImageZoomDialog(
            imageUrl = uiState.user?.profilePictureUrl,
            onDismissRequest = { showZoomedImage = false }
        )
    }
}