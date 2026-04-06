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
import com.acaris.features.auth.presentation.model.RegisterStep
import com.acaris.features.auth.presentation.viewmodel.RegisterViewModel

// Import komponen yang sudah kita pecah
import com.acaris.features.auth.ui.components.register.StepKodeKelas
import com.acaris.features.auth.ui.components.register.StepDataDiri
import com.acaris.features.auth.ui.components.register.StepOtp
import com.acaris.features.auth.ui.components.register.StepUploadDokumen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    role: String,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var errorDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.initRole(role) }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) { errorDialogMessage = state.errorMessage }
    }

    LaunchedEffect(state.currentStep) {
        if (state.currentStep == RegisterStep.SUCCESS_REGISTER) { showSuccessDialog = true }
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

    // Dialog Sukses
    if (showSuccessDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { },
            confirmText = "Lanjut ke Dashboard",
            onConfirm = { showSuccessDialog = false; onRegisterSuccess() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pendaftaran Berhasil!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Akun Anda telah siap digunakan.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp)) {
            Crossfade(targetState = state.currentStep, label = "Register Steps") { step ->
                when (step) {
                    RegisterStep.INPUT_KODE_KELAS -> StepKodeKelas(state.isLoading) { viewModel.submitKodeKelas(it) }
                    RegisterStep.INPUT_DATA_DIRI -> {
                        StepDataDiri(
                            role = role,
                            isLoading = state.isLoading,
                            onPhotoSelected = { file -> viewModel.onProfilePictureSelected(file) }, // 🌟 Tambahkan ini
                            onSubmitMahasiswa = viewModel::submitDataDiriMahasiswa,
                            onSubmitDosen = viewModel::submitDataDiriDosen,
                            onLoginClick = onNavigateToLogin
                        )
                    }
                    RegisterStep.INPUT_OTP -> StepOtp(state.isLoading) { viewModel.submitOtp(it) }
                    RegisterStep.UPLOAD_DOKUMEN -> {
                        StepUploadDokumen(
                            semester = viewModel.currentSemester,
                            isLoading = state.isLoading,
                            onUploadFile = { type, file, docSemester, onSuccess ->
                                viewModel.uploadDokumen(type, file, docSemester, onSuccess)
                            },
                            onFinish = { viewModel.finishDocumentUpload() }
                        )
                    }
                    RegisterStep.SUCCESS_REGISTER -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
            }
        }
    }
}