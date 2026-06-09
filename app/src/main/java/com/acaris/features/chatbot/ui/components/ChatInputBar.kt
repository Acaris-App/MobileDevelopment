package com.acaris.features.chatbot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.glowShadow

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var textState by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled = enabled && textState.isNotBlank()

    Surface(
        color = Color.Transparent, // Biarkan container luarnya transparan untuk efek overlay
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Kolom Input
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 120.dp)
                    // 🌟 FIX 1: URUTAN YANG BENAR (Glow dulu, baru Background)
                    .glowShadow(
                        color = MaterialTheme.colorScheme.secondary,
                        alpha = 0.4f,
                        blurRadius = 6.dp,
                        borderRadius = 24.dp
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (textState.isEmpty()) {
                        Text("Tanya Aca seputar akademik...", color = Color.Gray, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        enabled = enabled
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tombol Kirim
            CustomCircularIconButton(
                icon = Icons.Default.Send,
                contentDescription = "Kirim",
                color = if (isButtonEnabled) MaterialTheme.colorScheme.secondary else Color.Gray.copy(alpha = 0.5f),
                buttonSize = 48.dp,
                iconSize = 24.dp,
                showGlow = isButtonEnabled,
                onClick = {
                    if (isButtonEnabled) {
                        onSendMessage(textState)
                        textState = ""
                    }
                }
            )
        }
    }
}