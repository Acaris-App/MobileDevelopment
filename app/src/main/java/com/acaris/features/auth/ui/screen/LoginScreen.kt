package com.acaris.features.auth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomBackButton // 🌟 IMPORT INI
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.theme.AcarisTheme
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.auth.presentation.model.LoginState
import com.acaris.features.auth.presentation.viewmodel.LoginViewModel
import com.acaris.features.auth.ui.components.AuthTextField
import com.acaris.features.auth.ui.components.RoleSelectionSheet
import com.acaris.features.auth.ui.mapper.toUiModel
import com.acaris.features.auth.ui.model.UserUiModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onLoginSuccess: (UserUiModel) -> Unit,
    onNavigateToRegister: (String) -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    LoginScreenContent(
        loginState = loginState,
        onLoginClick = { email, password -> viewModel.login(email, password) },
        onResetState = { viewModel.resetState() },
        onBackClick = onBackClick,
        onLoginSuccess = onLoginSuccess,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword
    )
}

@Composable
fun LoginScreenContent(
    loginState: LoginState,
    onLoginClick: (String, String) -> Unit,
    onResetState: () -> Unit,
    onBackClick: () -> Unit,
    onLoginSuccess: (UserUiModel) -> Unit,
    onNavigateToRegister: (String) -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var showRoleSheet by rememberSaveable { mutableStateOf(false) }

    val isLoading = loginState is LoginState.Loading

    val isEmailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
    val isFormReady = ValidationUtils.isValidEmail(email) && password.isNotEmpty()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            CustomBackButton(onClick = onBackClick)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Masuk",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Silakan masukkan data kredensial Anda",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Masukkan email Anda",
                isError = isEmailError,
                errorMessage = "Format email tidak valid"
            )

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "********",
                isPassword = true
            )

            Text(
                text = "Lupa Password?",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onNavigateToForgotPassword() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomPrimaryButton(
                text = "Masuk",
                onClick = { onLoginClick(email, password) },
                enabled = isFormReady && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Belum punya akun? ",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )

                Text(
                    text = "Daftar",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showRoleSheet = true }
                        .padding(start = 4.dp)
                )
            }
        }
    }

    when (val state = loginState) {
        is LoginState.Success -> {
            val userUi = state.user.toUiModel()
            CustomDialog(
                showDialog = true,
                onDismissRequest = { },
                confirmText = "Lanjut ke Dashboard",
                onConfirm = {
                    onResetState()
                    onLoginSuccess(userUi)
                },
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Berhasil Masuk!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Halo, ${userUi.name}.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        }
        is LoginState.Error -> {
            CustomDialog(
                showDialog = true,
                onDismissRequest = { onResetState() },
                confirmText = "Coba Lagi",
                onConfirm = { onResetState() },
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Login Gagal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            )
        }
        else -> { }
    }

    RoleSelectionSheet(
        showSheet = showRoleSheet,
        onDismiss = { showRoleSheet = false },
        onRoleSelected = { role ->
            onNavigateToRegister(role)
        }
    )

    CustomLoadingOverlay(isLoading = isLoading)
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun LoginScreenPreview() {
    AcarisTheme {
        LoginScreenContent(
            loginState = LoginState.Idle,
            onLoginClick = { _, _ -> },
            onResetState = {},
            onBackClick = {},
            onLoginSuccess = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {}
        )
    }
}