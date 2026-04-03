package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.component.CustomOutlinedButton
import com.acaris.core.ui.component.CustomPrimaryButton
import com.acaris.core.ui.theme.AcarisTheme

// 1. Buat tipe dialog yang mungkin muncul
enum class DialogType {
    NONE, SUCCESS, FAILED
}

@Composable
fun Tester() {
    // 2. Sekarang kita cuma butuh SATU state, bukan dua!
    var currentDialog by remember { mutableStateOf(DialogType.NONE) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Area Testing Komponen", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            CustomOutlinedButton(
                text = "Munculkan Dialog Gagal",
                onClick = { currentDialog = DialogType.FAILED }, // Set state ke GAGAL
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomPrimaryButton(
                text = "Munculkan Dialog Sukses",
                onClick = { currentDialog = DialogType.SUCCESS }, // Set state ke SUKSES
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // ==========================================================
    // 3. LOGIKA RENDER DIALOG DINAMIS (CUMA DIPANGGIL 1 KALI!)
    // ==========================================================
    if (currentDialog != DialogType.NONE) {

        // --- Siapkan Variabel Dinamis ---
        val isSuccess = currentDialog == DialogType.SUCCESS

        val icon = if (isSuccess) Icons.Default.Check else Icons.Default.Close
        val iconBgColor = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
        val title = if (isSuccess) "Pendaftaran Berhasil" else "Opps, Terjadi Kesalahan"
        val message = if (isSuccess)
            "Akun Anda telah berhasil dibuat. Silakan lanjut login untuk masuk."
        else
            "NIM yang Anda masukkan sudah terdaftar. Silakan gunakan NIM lain."
        val btnText = if (isSuccess) "Lanjut Login" else "Coba Lagi"

        // --- Panggil CustomDialog ---
        CustomDialog(
            showDialog = true,
            onDismissRequest = { currentDialog = DialogType.NONE }, // Tutup dialog
            confirmText = btnText,
            onConfirm = {
                currentDialog = DialogType.NONE // Tutup dialog saat diklik

                // Nanti navigasinya ditaruh di sini berdasarkan kondisinya
                if (isSuccess) {
                    // contoh: navController.navigate(Screen.Login.route)
                }
            },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(72.dp).background(iconBgColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DialogTesterScreenPreview() {
    AcarisTheme {
        Tester()
    }
}