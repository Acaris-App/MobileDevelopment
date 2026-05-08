package com.acaris.features.user_management.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.auth.ui.components.AuthTextField
import com.acaris.features.user_management.presentation.viewmodel.EditUserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State Formulir Dasar
    var role by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var identifier by rememberSaveable { mutableStateOf("") }

    // State Formulir Spesifik Role (Mahasiswa/Dosen)
    var angkatan by rememberSaveable { mutableStateOf("") }
    var semester by rememberSaveable { mutableStateOf("") }
    var ipk by rememberSaveable { mutableStateOf("") }
    var dosenPa by rememberSaveable { mutableStateOf("") }
    var kodeKelas by rememberSaveable { mutableStateOf("") }

    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // State Foto Profil
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            selectedImageFile = ImageUtils.uriToFile(context, it)
        }
    }

    // 🌟 TRIGGER 1: Tarik data saat layar pertama kali dibuka
    LaunchedEffect(userId) {
        viewModel.loadInitialData(userId)
    }

    // 🌟 TRIGGER 2: Isi formulir otomatis saat data dari cache datang
    LaunchedEffect(uiState.initialUser) {
        uiState.initialUser?.let { user ->
            if (!isInitialized) {
                role = user.role.lowercase()
                name = user.name
                email = user.email
                identifier = user.identifier

                // Isi data tambahan jika ada (agar tidak null)
                angkatan = user.angkatan?.toString() ?: ""
                semester = user.currentSemester?.toString() ?: ""
                ipk = user.ipk?.toString() ?: ""
                dosenPa = user.dosenPa ?: ""
                kodeKelas = user.kodeKelas ?: ""

                isInitialized = true
            }
        }
    }

    // Validasi Real-time
    val isEmailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
    val isIdentifierError = identifier.isNotEmpty() && !identifier.all { it.isDigit() }

    val isFormReady = isInitialized &&
            name.isNotBlank() &&
            identifier.isNotBlank() && !isIdentifierError &&
            ValidationUtils.isValidEmail(email)

    // ==========================================
    // 🌟 RENDER DIALOG (KONFIRMASI, SUKSES & ERROR)
    // ==========================================
    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            confirmText = "Ya, Simpan",
            onConfirm = {
                showConfirmDialog = false
                viewModel.updateUser(
                    id = userId,
                    name = name,
                    email = email,
                    identifier = identifier,
                    angkatan = angkatan.toIntOrNull(),
                    currentSemester = semester.toIntOrNull(),
                    dosenPa = dosenPa.takeIf { it.isNotBlank() },
                    kodeKelas = kodeKelas.takeIf { it.isNotBlank() },
                    ipk = ipk.toDoubleOrNull(),
                    profilePicture = selectedImageFile
                )
            },
            dismissText = "Batal",
            onDismiss = { showConfirmDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Konfirmasi Perubahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin ingin menyimpan perubahan data pengguna ini?", textAlign = TextAlign.Center)
                }
            }
        )
    }

    if (uiState.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                viewModel.clearMessages()
                onNavigateBack() // Kembali setelah sukses
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "OK",
            onConfirm = {
                viewModel.clearMessages()
                onNavigateBack()
            }
        )
    }

    if (uiState.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Terjadi Kesalahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Tutup",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    // ==========================================
    // 🌟 RENDER LAYAR UTAMA
    // ==========================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Pengguna", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Hanya tampilkan form jika data awal sudah berhasil ditarik
            if (isInitialized) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // 🌟 Area Foto Profil
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(110.dp)
                            .clickable { photoLauncher.launch("image/*") }
                    ) {
                        // Prioritas Gambar: 1. Gambar baru (imageUri) -> 2. Gambar lama (profilePictureUrl)
                        val displayImage = imageUri ?: uiState.initialUser?.profilePictureUrl

                        if (displayImage != null) {
                            AsyncImage(
                                model = displayImage,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(65.dp))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ketuk untuk mengubah foto", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(32.dp))

                    // 🌟 Formulir Input Dasar
                    AuthTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = if (role == "mahasiswa") "NPM" else "NIP",
                        isError = isIdentifierError,
                        errorMessage = "Hanya boleh berisi angka"
                    )

                    AuthTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nama Lengkap"
                    )

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        isError = isEmailError,
                        errorMessage = "Format email tidak valid"
                    )

                    // 🌟 RENDER CONDITIONAL FIELD BERDASARKAN ROLE
                    if (role == "mahasiswa") {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Data Akademik Mahasiswa", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AuthTextField(value = angkatan, onValueChange = { angkatan = it }, label = "Angkatan", modifier = Modifier.weight(1f))
                            AuthTextField(value = semester, onValueChange = { semester = it }, label = "Semester", modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AuthTextField(value = ipk, onValueChange = { ipk = it }, label = "IPK", modifier = Modifier.weight(1f))
                            AuthTextField(value = kodeKelas, onValueChange = { kodeKelas = it }, label = "Kode Kelas", modifier = Modifier.weight(1f))
                        }

                        // 🔒 NAMA DOSEN PA DIKUNCI (READ-ONLY)
                        OutlinedTextField(
                            value = dosenPa,
                            onValueChange = {},
                            label = { Text("Dosen PA") },
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.DarkGray,
                                disabledBorderColor = Color.LightGray,
                                disabledLabelColor = Color.Gray
                            )
                        )

                    } else if (role == "dosen") {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Data Akademik Dosen", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Spacer(modifier = Modifier.height(8.dp))

                        // 🚫 KODE KELAS DOSEN DISEMBUNYIKAN
                        Text(
                            text = "Data kelas bimbingan diatur oleh sistem akademik.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    CustomPrimaryButton(
                        text = "Simpan Perubahan",
                        onClick = { showConfirmDialog = true },
                        enabled = isFormReady && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            // Tampilkan Loading jika sedang menarik data awal atau sedang proses update
            if (uiState.isLoading || !isInitialized) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}