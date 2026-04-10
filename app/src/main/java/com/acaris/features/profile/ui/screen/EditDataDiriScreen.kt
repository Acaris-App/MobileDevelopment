package com.acaris.features.profile.ui.screen

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    // 🌟 FIX 2: State untuk Scroll
    val scrollState = rememberScrollState()

    // State lokal untuk form input
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") }

    // 🌟 FIX 1: State Baru untuk Mahasiswa
    var angkatan by remember { mutableStateOf("") }
    var ipk by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }

    // LAUNCHER UNTUK UPLOAD FOTO (Memakai ImageUtils)
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val file = ImageUtils.uriToFile(context, uri)
            if (file != null) {
                profileViewModel.updateProfilePhoto(file)
            }
        }
    }

    // Isi form otomatis saat data profile tersedia
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
                    .verticalScroll(scrollState), // 🌟 FIX 2: Terapkan Scroll
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Judul & Keterangan
                Text("Edit Data Diri", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Perbarui informasi pribadi dan foto profil Anda.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Avatar dengan Icon Edit
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        /* * TODO: Jika kamu pakai library Coil nanti, ganti Icon ini dengan:
                         * AsyncImage(model = state.userProfile?.profilePictureUrl, contentDescription = null)
                         */
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                    }

                    IconButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .offset(x = 8.dp, y = 8.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ganti Foto", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Input Dasar
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
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text(if (state.userProfile?.role == "mahasiswa") "NPM" else "NIP") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                // 🌟 FIX 1: Tampilkan Field Tambahan HANYA untuk Mahasiswa
                if (state.userProfile?.role == "mahasiswa") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = angkatan,
                            onValueChange = { angkatan = it },
                            label = { Text("Angkatan") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        )
                        OutlinedTextField(
                            value = semester,
                            onValueChange = { semester = it },
                            label = { Text("Semester") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = ipk,
                        onValueChange = { ipk = it },
                        label = { Text("IPK") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Tombol Kirim di bawah
                CustomPrimaryButton(
                    text = "Kirim",
                    onClick = {
                        // 🌟 MEMANGGIL VIEWMODEL DENGAN DATA BARU
                        profileViewModel.updateProfile(
                            name = name,
                            email = email,
                            identifier = identifier,
                            angkatan = angkatan.toIntOrNull(),
                            ipk = ipk.toDoubleOrNull(),
                            semester = semester.toIntOrNull()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }

            // Tampilkan loading saat fetch profil atau upload foto
            if (state.isLoading || state.isUploadingPhoto) { CustomLoadingOverlay(isLoading = true) }
        }
    }
}