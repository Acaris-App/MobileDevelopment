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
import com.acaris.core.ui.components.CustomDropdownField // 🌟 IMPORT CUSTOM DROPDOWN
import com.acaris.core.ui.components.CustomImageZoomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.core.ui.components.CustomTextField // 🌟 IMPORT CUSTOM TEXT FIELD GLOBAL
import com.acaris.core.utils.ImageUtils
import com.acaris.core.utils.ValidationUtils
import com.acaris.features.user_management.presentation.viewmodel.EditUserViewModel
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // 🌟 STATE DROPDOWN MANUAL DIHAPUS KARENA SUDAH DI-HANDLE COMPONENT

    var showZoomedImage by remember { mutableStateOf(false) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val angkatanList = (currentYear downTo currentYear - 7).map { it.toString() }

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var selectedImageFile by remember { mutableStateOf<File?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            selectedImageFile = ImageUtils.uriToFile(context, it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadClasses()
    }

    LaunchedEffect(userId) {
        viewModel.loadInitialData(userId)
    }

    val isEmailError = uiState.email.isNotEmpty() && !ValidationUtils.isValidEmail(uiState.email)
    val isIdentifierError = uiState.identifier.isNotEmpty() && !uiState.identifier.all { it.isDigit() }

    val isFormReady = uiState.isFormInitialized &&
            uiState.name.isNotBlank() &&
            uiState.identifier.isNotBlank() && !isIdentifierError &&
            ValidationUtils.isValidEmail(uiState.email)

    if (showConfirmDialog) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { showConfirmDialog = false },
            confirmText = "Ya, Simpan",
            onConfirm = {
                showConfirmDialog = false
                viewModel.updateUser(userId = userId, profilePicture = selectedImageFile)
            },
            dismissText = "Batal",
            onDismiss = { showConfirmDialog = false },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Konfirmasi Perubahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Apakah Anda yakin ingin menyimpan perubahan data pengguna ini?", textAlign = TextAlign.Center)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Pengguna", fontWeight = FontWeight.Bold) },
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

            if (uiState.isFormInitialized) {
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
                            .clickable { showZoomedImage = true }
                    ) {
                        val displayImage = imageUri ?: uiState.initialUser?.profilePictureUrl

                        if (displayImage != null) {
                            AsyncImage(
                                model = displayImage,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.2.dp, MaterialTheme.colorScheme.primary, CircleShape),
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
                            icon = Icons.Default.Edit,
                            contentDescription = "Edit Foto Profil",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .offset(x = 4.dp, y = 4.dp)
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.background, CircleShape),
                            onClick = { photoLauncher.launch("image/*") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ketuk untuk memperbesar foto", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(32.dp))

                    // 🌟 MENGGUNAKAN CUSTOM TEXT FIELD
                    CustomTextField(
                        value = uiState.identifier,
                        onValueChange = { viewModel.onIdentifierChanged(it) },
                        label = if (uiState.role == "mahasiswa") "NPM" else "NIP",
                        isError = isIdentifierError,
                        errorMessage = "Hanya boleh berisi angka",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    CustomTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = "Nama Lengkap"
                    )

                    CustomTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        label = "Email",
                        isError = isEmailError,
                        errorMessage = "Format email tidak valid",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    if (uiState.role == "mahasiswa") {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Data Akademik Mahasiswa", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Spacer(modifier = Modifier.height(8.dp))

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
                                onValueChange = { viewModel.onIpkChanged(it) },
                                label = "IPK",
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 🌟 KODE KELAS DENGAN CUSTOM DROPDOWN
                        val displayValue = if (uiState.kodeKelas.isNotBlank() && uiState.dosenPa.isNotBlank())
                            "${uiState.kodeKelas} - ${uiState.dosenPa}"
                        else
                            uiState.kodeKelas

                        CustomDropdownField(
                            value = displayValue,
                            options = uiState.availableClasses,
                            onOptionSelected = { classInfo ->
                                viewModel.onClassSelected(classInfo.kodeKelas, classInfo.dosenPa)
                            },
                            optionLabelProvider = { "${it.kodeKelas} - ${it.dosenPa}" },
                            label = "Kode Kelas & Dosen Pembimbing",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    CustomPrimaryButton(
                        text = "Simpan Perubahan",
                        onClick = { showConfirmDialog = true },
                        enabled = isFormReady && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            if (showZoomedImage) {
                val displayImage = imageUri ?: uiState.initialUser?.profilePictureUrl
                CustomImageZoomDialog(
                    imageUrl = displayImage?.toString(),
                    onDismissRequest = { showZoomedImage = false }
                )
            }

            if (uiState.isLoading || !uiState.isFormInitialized) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}