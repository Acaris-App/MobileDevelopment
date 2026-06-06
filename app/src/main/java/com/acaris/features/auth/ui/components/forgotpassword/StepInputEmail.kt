package com.acaris.features.auth.ui.components.forgotpassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField
import com.acaris.core.utils.ValidationUtils // 🌟 IMPORT VALIDATION UTILS

@Composable
fun StepInputEmail(
    isLoading: Boolean,
    onSubmit: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val isEmailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
    val isFormReady = ValidationUtils.isValidEmail(email)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tolong masukan email terdaftar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Masukkan email terdaftar",
                isError = isEmailError,
                errorMessage = "Format email tidak valid",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CustomPrimaryButton(
                text = "Selanjutnya",
                onClick = { onSubmit(email) },
                enabled = isFormReady && !isLoading,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
        CustomLoadingOverlay(isLoading = isLoading)
    }
}