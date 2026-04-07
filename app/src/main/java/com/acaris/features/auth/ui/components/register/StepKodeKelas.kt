package com.acaris.features.auth.ui.components.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.features.auth.ui.components.AuthTextField

@Composable
fun StepKodeKelas(isLoading: Boolean, onSubmit: (String) -> Unit) {
    var kode by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader("Kode Kelas", "Masukkan kode unik yang diberikan oleh Dosen Pembimbing Akademik Anda.")

        AuthTextField(
            value = kode,
            onValueChange = { kode = it },
            label = "Kode Kelas",
            placeholder = "Masukkan Kode"
        )

        Spacer(modifier = Modifier.height(48.dp))

        CustomPrimaryButton(
            text = if (isLoading) "Memeriksa..." else "Selanjutnya \u2192",
            onClick = { onSubmit(kode) },
            enabled = kode.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }

    CustomLoadingOverlay(isLoading = isLoading)
}