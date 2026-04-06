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
import coil.compose.AsyncImage
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.auth.ui.components.AuthTextField
import java.io.File

@Composable
fun StepDataDiri(
    role: String,
    isLoading: Boolean,
    onPhotoSelected: (File) -> Unit,
    onSubmitMahasiswa: (String, String, String, String, Int, Int) -> Unit,
    onSubmitDosen: (String, String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val file = ImageUtils.uriToFile(context, it)
            if (file != null) onPhotoSelected(file)
        }
    }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var npm by rememberSaveable { mutableStateOf("") }
    var angkatan by rememberSaveable { mutableStateOf("") }
    var semester by rememberSaveable { mutableStateOf("") }
    var nip by rememberSaveable { mutableStateOf("") }

    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val isEmailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
    val isPasswordError = password.isNotEmpty() && !ValidationUtils.isValidPassword(password)
    val isConfirmPasswordError = confirmPassword.isNotEmpty() && password != confirmPassword

    val isFormReady = name.isNotBlank() &&
            ValidationUtils.isValidEmail(email) &&
            ValidationUtils.isValidPassword(password) &&
            password == confirmPassword &&
            if (role == "mahasiswa") {
                npm.isNotBlank() && angkatan.isNotBlank() && semester.isNotBlank()
            } else {
                nip.isNotBlank()
            }

    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            confirmText = "Ya, Lanjutkan",
            onConfirm = {
                showConfirmDialog = false
                if (role == "mahasiswa") {
                    onSubmitMahasiswa(npm, name, email, password, angkatan.toIntOrNull() ?: 0, semester.toIntOrNull() ?: 0)
                } else {
                    onSubmitDosen(nip, name, email, password)
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
        SectionHeader("Data Diri", "Lengkapi profil Anda")

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(110.dp)
                .clickable { photoLauncher.launch("image/*") }
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
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(65.dp))
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (role == "mahasiswa") {
            AuthTextField(value = npm, onValueChange = { npm = it }, label = "NPM")
        } else {
            AuthTextField(value = nip, onValueChange = { nip = it }, label = "NIP")
        }

        AuthTextField(value = name, onValueChange = { name = it }, label = "Nama Lengkap")

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            isError = isEmailError,
            errorMessage = "Format email tidak valid"
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            isError = isPasswordError,
            errorMessage = "Password minimal 8 karakter"
        )

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Konfirmasi Password",
            isPassword = true,
            isError = isConfirmPasswordError,
            errorMessage = "Password tidak cocok"
        )

        if (role == "mahasiswa") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AuthTextField(value = angkatan, onValueChange = { angkatan = it }, label = "Angkatan", modifier = Modifier.weight(1f))
                AuthTextField(value = semester, onValueChange = { semester = it }, label = "Semester", modifier = Modifier.weight(1f))
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
            Text("Sudah punya akun? ", color = Color.Gray)
            Text("Masuk", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}