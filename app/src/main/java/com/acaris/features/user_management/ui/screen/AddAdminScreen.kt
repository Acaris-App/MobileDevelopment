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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField // 🌟 IMPORT CUSTOM TEXT FIELD
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.user_management.presentation.viewmodel.AddAdminViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdminScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddAdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var nip by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }

    var showZoomedImage by remember { mutableStateOf(false) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            selectedImageFile = ImageUtils.uriToFile(context, it)
        }
    }

    // Validasi Real-time
    val isEmailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
    val isPasswordError = password.isNotEmpty() && !ValidationUtils.isValidPassword(password)
    val isConfirmPasswordError = confirmPassword.isNotEmpty() && password != confirmPassword
    val isNipError = nip.isNotEmpty() && !nip.all { it.isDigit() }

    val isFormReady = name.isNotBlank() &&
            ValidationUtils.isValidEmail(email) &&
            ValidationUtils.isValidPassword(password) &&
            password == confirmPassword &&
            nip.isNotBlank() && !isNipError

    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            confirmText = "Ya, Tambahkan",
            onConfirm = {
                showConfirmDialog = false
                viewModel.addAdmin(name, email, nip, password, selectedImageFile)
            },
            dismissText = "Batal",
            onDismiss = { showConfirmDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Konfirmasi Penambahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin ingin memberikan hak akses Admin kepada pengguna ini?", textAlign = TextAlign.Center)
                }
            }
        )
    }

    if (uiState.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                viewModel.clearMessages()
                onNavigateBack()
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
    // RENDER LAYAR UTAMA
    // ==========================================
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Admin", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .size(110.dp)
                        .clickable {
                            if (imageUri != null) {
                                showZoomedImage = true
                            } else {
                                photoLauncher.launch("image/*")
                            }
                        }
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
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

                    CustomCircularIconButton(
                        icon = if (imageUri == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = "Pilih Foto",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.background, CircleShape),
                        onClick = { photoLauncher.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (imageUri == null) "Foto Profil (Opsional)" else "Ketuk untuk memperbesar foto",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = "NIP",
                    placeholder = "Nomor Induk Pegawai",
                    isError = isNipError,
                    errorMessage = "NIP harus berupa angka",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama Lengkap",
                    placeholder = "Masukkan nama lengkap admin"
                )

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Masukkan email valid",
                    isError = isEmailError,
                    errorMessage = "Format email tidak valid",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Minimal 8 karakter",
                    isPassword = true,
                    isError = isPasswordError,
                    errorMessage = "Password minimal 8 karakter"
                )

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Konfirmasi Password",
                    placeholder = "Masukkan konfirmasi password",
                    isPassword = true,
                    isError = isConfirmPasswordError,
                    errorMessage = "Password tidak cocok"
                )

                Spacer(modifier = Modifier.height(40.dp))

                CustomPrimaryButton(
                    text = "Simpan Admin",
                    onClick = { showConfirmDialog = true },
                    enabled = isFormReady && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (showZoomedImage && imageUri != null) {
                CustomImageZoomDialog(
                    imageUrl = imageUri.toString(),
                    onDismissRequest = { showZoomedImage = false }
                )
            }

            if (uiState.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}