package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun <T : Any> CustomFloatingDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<T>,
    selectedOption: T? = null,
    optionLabelProvider: (T) -> String,
    optionIconProvider: ((T) -> ImageVector)? = null,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    if (expanded) {
        // 🌟 FIX 1: Mengambil kepadatan layar untuk menghitung posisi (offset)
        val density = LocalDensity.current
        val yOffset = with(density) { 48.dp.roundToPx() } // Geser ke bawah 48dp
        val xOffset = with(density) { (-4).dp.roundToPx() } // Geser ke kiri 4dp

        Popup(
            alignment = Alignment.TopEnd,
            // 🌟 FIX 2: Menggunakan offset alih-alih padding agar area sentuh tidak melar
            offset = IntOffset(xOffset, yOffset),
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            Column(
                // 🌟 FIX 3: Padding di sini sudah Dihapus!
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption

                    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface
                    val contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurface

                    Box(
                        modifier = Modifier
                            .shadow(elevation = 6.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(backgroundColor)
                            .clickable {
                                onOptionSelected(option)
                                onDismissRequest()
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            optionIconProvider?.invoke(option)?.let { icon ->
                                CustomCircularIconButton(
                                    icon = icon,
                                    contentDescription = "",
                                    color = contentColor,
                                    modifier = Modifier.size(28.dp),
                                    onClick = {
                                        onOptionSelected(option)
                                        onDismissRequest()
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = optionLabelProvider(option),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = contentColor,
                                fontSize = 13.sp
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = contentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}