package com.acaris.features.profile.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.utils.ImageUtils
import com.acaris.features.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDataDiriScreen(
    onNavigateBack: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val state by profileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }
    var angkatan by remember { mutableStateOf("") }
    var ipk by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    LaunchedEffect(state.userProfile) {
        state.userProfile?.let {
            name = it.name
            email = it.email
            identifier = it.identifier
            angkatan = it.angkatan?.toString() ?: ""
            ipk = it.ipk?.toString() ?: ""
            semester = it.currentSemester?.toString() ?: ""
        }
    }

    // DIALOG KONFIRMASI SIMPAN
    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Konfirmasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Apakah Anda yakin ingin menyimpan perubahan data diri ini?",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            },
            confirmText = "Simpan",
            onConfirm = {
                showConfirmDialog = false

                val textChanged = state.userProfile?.let {
                    name != it.name ||
                            identifier != it.identifier ||
                            angkatan != (it.angkatan?.toString() ?: "") ||
                            ipk != (it.ipk?.toString() ?: "") ||
                            semester != (it.currentSemester?.toString() ?: "")
                } ?: true

                if (textChanged) {
                    profileViewModel.updateProfile(
                        name = name,
                        email = email,
                        identifier = identifier,
                        angkatan = angkatan.toIntOrNull(),
                        ipk = ipk.toDoubleOrNull(),
                        semester = semester.toIntOrNull()
                    )
                }

                selectedPhotoUri?.let { uri ->
                    val file = ImageUtils.uriToFile(context, uri)
                    if (file != null) {
                        profileViewModel.updateProfilePhoto(file)
                    }
                }
            },
            dismissText = "Batal",
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (state.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { profileViewModel.clearMessages() },
            confirmText = "Tutup",
            onConfirm = { profileViewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Gagal Menyimpan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(state.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        )
    }

    if (state.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                profileViewModel.clearMessages()
                onNavigateBack()
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.successMessage ?: "",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                }
            },
            confirmText = "OK",
            onConfirm = {
                profileViewModel.clearMessages()
                onNavigateBack()
            }
        )
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
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Edit Data Diri", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Perbarui informasi pribadi dan foto profil Anda.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageToLoad = selectedPhotoUri ?: state.userProfile?.profilePictureUrl

                        if (imageToLoad == null || imageToLoad.toString().isEmpty()) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                        } else {
                            AsyncImage(
                                model = imageToLoad,
                                contentDescription = "Foto Profil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    IconButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .offset(x = 8.dp, y = 8.dp)
                            .border(1.dp, Color.Transparent, CircleShape)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil", tint = MaterialTheme.colorScheme.background)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("Email (Tidak dapat diubah)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFB71C1C),
                        focusedBorderColor = Color(0xFFB71C1C),
                        unfocusedLabelColor = Color(0xFFB71C1C),
                        focusedLabelColor = Color(0xFFB71C1C)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text(if (state.userProfile?.role == "mahasiswa") "NPM" else "NIP") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (state.userProfile?.role == "mahasiswa") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(value = angkatan, onValueChange = { angkatan = it }, label = { Text("Angkatan") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium)
                        OutlinedTextField(value = semester, onValueChange = { semester = it }, label = { Text("Semester") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = ipk, onValueChange = { ipk = it }, label = { Text("IPK") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)
                }

                Spacer(modifier = Modifier.height(40.dp))

                CustomPrimaryButton(
                    text = "Kirim",
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }

            if (state.isLoading || state.isUploadingPhoto) { CustomLoadingOverlay(isLoading = true) }
        }
    }
}