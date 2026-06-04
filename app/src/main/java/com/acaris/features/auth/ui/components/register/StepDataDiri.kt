package com.acaris.features.auth.ui.components.register

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
import androidx.compose.material.icons.filled.Edit // 🌟 IMPOR ICON EDIT
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.sp // 🌟 IMPOR UNTUK FONT SIZE
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomImageZoomDialog // 🌟 IMPOR KOMPONEN ZOOM DIALOG
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.auth.presentation.viewmodel.RegisterViewModel
import com.acaris.features.auth.ui.components.AuthTextField
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepDataDiri(
    role: String,
    isLoading: Boolean,
    onPhotoSelected: (File) -> Unit,
    onSubmitMahasiswa: (String, String, String, String, Int, Int, Double) -> Unit,
    onSubmitDosen: (String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // 🌟 STATE BARU: Melacak status Zoom Gambar
    var showZoomedImage by remember { mutableStateOf(false) }

    // State Dropdown
    var isAngkatanDropdownExpanded by remember { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val angkatanList = (currentYear downTo currentYear - 7).map { it.toString() }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val file = ImageUtils.uriToFile(context, it)
            if (file != null) onPhotoSelected(file)
        }
    }

    val isEmailError = uiState.email.isNotEmpty() && !ValidationUtils.isValidEmail(uiState.email)
    val isPasswordError = uiState.password.isNotEmpty() && !ValidationUtils.isValidPassword(uiState.password)
    val isConfirmPasswordError = uiState.confirmPassword.isNotEmpty() && uiState.password != uiState.confirmPassword
    val isNpmError = uiState.npm.isNotEmpty() && !uiState.npm.all { it.isDigit() }
    val isNipError = uiState.nip.isNotEmpty() && !uiState.nip.all { it.isDigit() }
    val ipkValue = uiState.ipk.toDoubleOrNull()
    val isIpkError = uiState.ipk.isNotEmpty() && (ipkValue == null || ipkValue < 0.0 || ipkValue > 4.0)

    val isFormReady = uiState.name.isNotBlank() &&
            ValidationUtils.isValidEmail(uiState.email) &&
            ValidationUtils.isValidPassword(uiState.password) &&
            uiState.password == uiState.confirmPassword &&
            if (role == "mahasiswa") {
                uiState.npm.isNotBlank() && !isNpmError &&
                        uiState.angkatan.isNotBlank() &&
                        uiState.semester.isNotBlank() &&
                        uiState.ipk.isNotBlank() && !isIpkError
            } else {
                uiState.nip.isNotBlank() && !isNipError
            }

    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            confirmText = "Ya, Lanjutkan",
            onConfirm = {
                showConfirmDialog = false
                if (role == "mahasiswa") {
                    onSubmitMahasiswa(
                        uiState.npm, uiState.name, uiState.email, uiState.password,
                        uiState.angkatan.toIntOrNull() ?: 0,
                        uiState.semester.toIntOrNull() ?: 0,
                        uiState.ipk.toDoubleOrNull() ?: 0.0
                    )
                } else {
                    onSubmitDosen(uiState.nip, uiState.name, uiState.email, uiState.password)
                }
            },
            dismissText = "Cek Kembali",
            onDismiss = { showConfirmDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Konfirmasi Data", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin semua data diri sudah benar? Pastikan email aktif untuk menerima OTP.", textAlign = TextAlign.Center)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader("Lengkapi profil Anda")

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(110.dp)
                .clickable {
                    // 🌟 LOGIKA PINTAR: Buka galeri jika kosong, Zoom jika sudah ada
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
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(65.dp)
                    )
                }
            }

            CustomCircularIconButton(
                // 🌟 IKON DINAMIS: Add jika kosong, Edit jika sudah ada
                icon = if (imageUri == null) Icons.Default.Add else Icons.Default.Edit,
                contentDescription = "Pilih Foto",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .offset(x = 4.dp, y = 4.dp)
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                onClick = { photoLauncher.launch("image/*") } // 🌟 Tombol pena selalu buka galeri
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        // 🌟 TEKS INSTRUKSI DINAMIS
        Text(
            text = if (imageUri == null) "Foto Profil (Opsional)" else "Ketuk untuk memperbesar foto",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (role == "mahasiswa") {
            AuthTextField(
                value = uiState.npm,
                onValueChange = { viewModel.onNpmChanged(it) },
                label = "NPM",
                isError = isNpmError,
                errorMessage = "NPM harus berupa angka"
            )
        } else {
            AuthTextField(
                value = uiState.nip,
                onValueChange = { viewModel.onNipChanged(it) },
                label = "NIP",
                isError = isNipError,
                errorMessage = "NIP harus berupa angka"
            )
        }

        AuthTextField(value = uiState.name, onValueChange = { viewModel.onNameChanged(it) }, label = "Nama Lengkap")

        AuthTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Email",
            isError = isEmailError,
            errorMessage = "Format email tidak valid"
        )

        AuthTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Password",
            isPassword = true,
            isError = isPasswordError,
            errorMessage = "Password minimal 8 karakter"
        )

        AuthTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            label = "Konfirmasi Password",
            isPassword = true,
            isError = isConfirmPasswordError,
            errorMessage = "Password tidak cocok"
        )

        if (role == "mahasiswa") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // DROPDOWN ANGKATAN
                ExposedDropdownMenuBox(
                    expanded = isAngkatanDropdownExpanded,
                    onExpandedChange = { isAngkatanDropdownExpanded = !isAngkatanDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = uiState.angkatan,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Angkatan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAngkatanDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = isAngkatanDropdownExpanded,
                        onDismissRequest = { isAngkatanDropdownExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        angkatanList.forEach { thn ->
                            DropdownMenuItem(
                                text = { Text(thn) },
                                onClick = {
                                    viewModel.onAngkatanChanged(thn)
                                    isAngkatanDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // SEMESTER (READ-ONLY)
                OutlinedTextField(
                    value = uiState.semester,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Semester") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )

                // IPK
                OutlinedTextField(
                    value = uiState.ipk,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                            viewModel.onIpkChanged(newValue)
                        }
                    },
                    label = { Text("IPK") },
                    isError = isIpkError,
                    supportingText = { if (isIpkError) Text("Maks 4.00", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomPrimaryButton(
            text = if (isLoading) "Loading..." else "Selanjutnya \u2192",
            onClick = { showConfirmDialog = true },
            enabled = isFormReady && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sudah punya akun? ", color = MaterialTheme.colorScheme.onBackground)
            Text("Masuk", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    // 🌟 PANGGIL KOMPONEN DIALOG MENGGUNAKAN BLOK IF
    if (showZoomedImage && imageUri != null) {
        CustomImageZoomDialog(
            imageUrl = imageUri.toString(),
            onDismissRequest = { showZoomedImage = false }
        )
    }

    CustomLoadingOverlay(isLoading = isLoading)
}