package com.acaris.features.profile.ui.screen

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
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.features.documents_mahasiswa.presentation.viewmodel.DocumentViewModel
import com.acaris.features.profile.presentation.viewmodel.ProfileViewModel
import com.acaris.features.profile.ui.component.ProfileInfoCard
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.acaris.core.ui.components.CustomPrimaryButton

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
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(24.dp))

                profileState.userProfile?.let { user ->
                    ProfileInfoCard(
                        userProfile = user,
                        onEditClick = onNavigateToEditDataDiri
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (profileState.userProfile?.role == "mahasiswa") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.padding(24.dp)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Dokumen Akademik", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))

                                documentState.documents.forEachIndexed { index, doc ->
                                    com.acaris.features.documents_mahasiswa.ui.components.DocumentCard(
                                        document = doc,
                                        onClick = {
                                            if (doc.fileUrl.isNotEmpty()) uriHandler.openUri(doc.fileUrl)
                                        },
                                        showDelete = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                if (documentState.documents.isEmpty()) {
                                    Text("Belum ada dokumen yang diunggah.", color = Color.Gray)
                                }
                            }

                            IconButton(
                                onClick = onNavigateToEditDokumen,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 12.dp, y = 12.dp)
                                    .border(1.dp, Color.Transparent, CircleShape)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profil", tint = MaterialTheme.colorScheme.background)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                CustomPrimaryButton(
                    text = "Ganti Password",
                    onClick = onNavigateToChangePassword,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(120.dp))
            }

            if (profileState.isLoading || documentState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}