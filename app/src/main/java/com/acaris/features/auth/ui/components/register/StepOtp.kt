package com.acaris.features.auth.ui.components.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomPrimaryButton
import com.acaris.features.auth.ui.components.OtpTextField
import kotlinx.coroutines.delay // 🌟 Pastikan ini di-import

@Composable
fun StepOtp(
    isLoading: Boolean,
    onSubmit: (String) -> Unit,
    onResendClick: () -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var timeLeft by rememberSaveable { mutableIntStateOf(60) }
    var isResendEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(timeLeft, isResendEnabled) {
        if (!isResendEnabled && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            isResendEnabled = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Verifikasi OTP", "Masukkan 6 digit kode yang telah kami kirimkan ke email Anda.")

        OtpTextField(
            otpText = otp,
            onOtpChange = { otp = it },
            otpCount = 6,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f).padding(end = 16.dp), color = Color.Gray.copy(alpha = 0.3f))

            if (isResendEnabled) {
                Text(
                    text = "Kirim Ulang",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        isResendEnabled = false
                        timeLeft = 60

                        onResendClick()
                    }
                )
            } else {
                Text(
                    text = "Kirim ulang dalam ${timeLeft}s",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        CustomPrimaryButton(
            text = if (isLoading) "Memverifikasi..." else "Selanjutnya \u2192",
            onClick = { onSubmit(otp) },
            enabled = otp.length == 6 && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }

    CustomLoadingOverlay(isLoading = isLoading)
}