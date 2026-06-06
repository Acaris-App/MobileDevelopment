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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomTextField
import com.acaris.core.ui.components.CustomDropdownField
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.auth.presentation.viewmodel.RegisterViewModel
import java.io.File
import java.util.Calendar

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
    var showZoomedImage by remember { mutableStateOf(false) }

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

        if (role == "mahasiswa") {
            CustomTextField(
                value = uiState.npm,
                onValueChange = { viewModel.onNpmChanged(it) },
                label = "NPM",
                placeholder = "Nomor Pokok Mahasiswa",
                isError = isNpmError,
                errorMessage = "NPM harus berupa angka",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
            CustomTextField(
                value = uiState.nip,
                onValueChange = { viewModel.onNipChanged(it) },
                label = "NIP",
                placeholder = "Nomor Induk Pegawai",
                isError = isNipError,
                errorMessage = "NIP harus berupa angka",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        CustomTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChanged(it) },
            label = "Nama Lengkap",
            placeholder = "Masukkan nama lengkap Anda"
        )

        CustomTextField(
            value = uiState.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = "Email",
            placeholder = "Masukkan email valid Anda",
            isError = isEmailError,
            errorMessage = "Format email tidak valid",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        CustomTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = "Password",
            placeholder = "Minimal 8 karakter",
            isPassword = true,
            isError = isPasswordError,
            errorMessage = "Password minimal 8 karakter"
        )

        CustomTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChanged(it) },
            label = "Konfirmasi Password",
            placeholder = "Masukkan ulang password",
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
                CustomDropdownField(
                    value = uiState.angkatan,
                    options = angkatanList,
                    onOptionSelected = { viewModel.onAngkatanChanged(it) },
                    optionLabelProvider = { it },
                    label = "Angkatan",
                    modifier = Modifier.weight(1f)
                )

                CustomTextField(
                    value = uiState.semester,
                    onValueChange = {},
                    label = "Semester",
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                )

                CustomTextField(
                    value = uiState.ipk,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                            viewModel.onIpkChanged(newValue)
                        }
                    },
                    label = "IPK",
                    isError = isIpkError,
                    errorMessage = "Maks 4.00",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
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

    if (showZoomedImage && imageUri != null) {
        CustomImageZoomDialog(
            imageUrl = imageUri.toString(),
            onDismissRequest = { showZoomedImage = false }
        )
    }

    CustomLoadingOverlay(isLoading = isLoading)
}