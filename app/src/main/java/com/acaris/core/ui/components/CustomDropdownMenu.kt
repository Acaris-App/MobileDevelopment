package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

// REGULAR DROPDOWN FIELD
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomDropdownField(
    value: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    optionLabelProvider: (T) -> String,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true, // Selalu readOnly karena input dari dropdown
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabelProvider(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// FLOATING DROPDOWN
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
        val density = LocalDensity.current
        val yOffset = with(density) { 48.dp.roundToPx() }
        val xOffset = with(density) { (-4).dp.roundToPx() }

        Popup(
            alignment = Alignment.TopEnd,
            offset = IntOffset(xOffset, yOffset),
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            Column(
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