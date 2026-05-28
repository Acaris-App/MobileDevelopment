package com.acaris.features.profile.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.documents_mahasiswa.presentation.viewmodel.DocumentViewModel
import com.acaris.features.profile.presentation.viewmodel.ProfileViewModel
import com.acaris.features.profile.ui.components.ProfileInfoCard

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditDataDiri: () -> Unit,
    onNavigateToEditDokumen: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val documentState by documentViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Data Diri", "Dokumen Akademik")

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
                            // TAB 2: DOKUMEN
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Box(modifier = Modifier.padding(24.dp)) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Berkas Dokumen",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            CustomCircularIconButton(
                                                icon = Icons.Default.Edit,
                                                contentDescription = "Edit Dokumen",
                                                color = MaterialTheme.colorScheme.primary,
                                                onClick = onNavigateToEditDokumen,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))

                                        documentState.documents.forEachIndexed { index, doc ->
                                            com.acaris.features.documents_mahasiswa.ui.components.DocumentCard(
                                                document = doc,
                                                onClick = {
                                                    if (doc.fileUrl.isNotEmpty()) uriHandler.openUri(doc.fileUrl)
                                                },
                                                showDelete = false,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }

                                        if (documentState.documents.isEmpty()) {
                                            Text("Belum ada dokumen yang diunggah.", color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // JIKA BUKAN MAHASISWA, TAMPILKAN LANGSUNG
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