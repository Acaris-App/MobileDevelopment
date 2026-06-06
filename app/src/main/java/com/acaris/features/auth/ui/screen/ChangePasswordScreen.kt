package com.acaris.features.auth.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField // 🌟 IMPORT CUSTOM TEXT FIELD GLOBAL KITA
import com.acaris.features.auth.presentation.viewmodel.ChangePasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val isNewPasswordError = newPassword.isNotEmpty() && newPassword.length < 8
    val isConfirmPasswordError = confirmPassword.isNotEmpty() && confirmPassword != newPassword
    val isSameAsOldError = newPassword.isNotEmpty() && oldPassword == newPassword

    val isFormValid = oldPassword.isNotBlank() &&
            newPassword.length >= 8 &&
            newPassword == confirmPassword &&
            oldPassword != newPassword

    if (state.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = {
                viewModel.clearMessages()
                onNavigateBack()
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "Tutup",
            onConfirm = {
                viewModel.clearMessages()
                onNavigateBack()
            }
        )
    }

    if (state.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gagal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.errorMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "Coba Lagi",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ganti Password",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    CustomBackButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
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
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Buat password baru yang kuat dan unik untuk mengamankan akun Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                CustomTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = "Password Saat Ini",
                    placeholder = "Masukkan password saat ini",
                    isPassword = true
                )

                val newPasswordErrorMessage = when {
                    isNewPasswordError -> "Password minimal terdiri dari 8 karakter."
                    isSameAsOldError -> "Password baru tidak boleh sama dengan password saat ini."
                    else -> ""
                }

                CustomTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Password Baru",
                    placeholder = "Masukan password baru",
                    isPassword = true,
                    isError = isNewPasswordError || isSameAsOldError,
                    errorMessage = newPasswordErrorMessage
                )

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Konfirmasi Password Baru",
                    placeholder = "Masukkan konfirmasi password baru",
                    isPassword = true,
                    isError = isConfirmPasswordError,
                    errorMessage = "Password tidak cocok."
                )

                Spacer(modifier = Modifier.height(48.dp))

                CustomPrimaryButton(
                    text = "Kirim",
                    enabled = isFormValid && !state.isLoading,
                    onClick = {
                        viewModel.submitChangePassword(oldPassword, newPassword, confirmPassword)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }

            if (state.isLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}