package com.acaris.features.auth.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.acaris.core.ui.components.CustomDialog
import com.acaris.features.auth.presentation.model.ForgotPasswordStep
import com.acaris.features.auth.presentation.viewmodel.ForgotPasswordViewModel
import com.acaris.features.auth.ui.components.forgotpassword.StepInputEmail
import com.acaris.features.auth.ui.components.forgotpassword.StepInputNewPassword
import com.acaris.features.auth.ui.components.register.StepOtp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var errorDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) { errorDialogMessage = state.errorMessage }
    }

    LaunchedEffect(state.currentStep) {
        if (state.currentStep == ForgotPasswordStep.SUCCESS) { showSuccessDialog = true }
    }

    // Dialog Error
    if (errorDialogMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { errorDialogMessage = null; viewModel.clearError() },
            confirmText = "Tutup",
            onConfirm = { errorDialogMessage = null; viewModel.clearError() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Terjadi Kesalahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorDialogMessage ?: "", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        )
    }

    // Dialog Success
    if (showSuccessDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { },
            confirmText = "Kembali ke Login",
            onConfirm = { showSuccessDialog = false; onResetSuccess() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Password Diperbarui!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Silakan login menggunakan password baru Anda.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.currentStep == ForgotPasswordStep.INPUT_EMAIL) {
                            onNavigateBack()
                        } else {
                            viewModel.navigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp)) {
            Crossfade(targetState = state.currentStep, label = "Forgot Password Steps") { step ->
                when (step) {
                    ForgotPasswordStep.INPUT_EMAIL -> {
                        StepInputEmail(
                            isLoading = state.isLoading,
                            onSubmit = { email -> viewModel.requestOtp(email) }
                        )
                    }
                    ForgotPasswordStep.INPUT_OTP -> {
                        StepOtp(
                            isLoading = state.isLoading,
                            onResendClick = { viewModel.requestOtp(state.email) },
                            onSubmit = { otp -> viewModel.verifyOtp(otp) }
                        )
                    }
                    ForgotPasswordStep.INPUT_NEW_PASSWORD -> {
                        StepInputNewPassword(
                            isLoading = state.isLoading,
                            onSubmit = { newPass, confirmPass -> viewModel.resetPassword(newPass, confirmPass) }
                        )
                    }
                    ForgotPasswordStep.SUCCESS -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}