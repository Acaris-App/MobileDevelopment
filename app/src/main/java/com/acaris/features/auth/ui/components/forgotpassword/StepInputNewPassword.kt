package com.acaris.features.auth.ui.components.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField

@Composable
fun StepInputNewPassword(
    isLoading: Boolean,
    onSubmit: (String, String) -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val isPasswordError = newPassword.isNotEmpty() && newPassword.length < 8
    val isConfirmError = confirmPassword.isNotEmpty() && confirmPassword != newPassword

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Buat kombinasi password yang kuat",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(60.dp))

            CustomTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "Password Baru",
                placeholder = "masukan password baru",
                isPassword = true,
                isError = isPasswordError,
                errorMessage = "Password minimal terdiri dari 8 karakter",
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Konfirmasi Password",
                placeholder = "masukkan konfirmasi password baru",
                isPassword = true,
                isError = isConfirmError,
                errorMessage = "Konfirmasi password tidak cocok",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            CustomPrimaryButton(
                text = "Kirim",
                onClick = { onSubmit(newPassword, confirmPassword) },
                enabled = newPassword.length >= 8 && confirmPassword == newPassword && !isLoading,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
        CustomLoadingOverlay(isLoading = isLoading)
    }
}