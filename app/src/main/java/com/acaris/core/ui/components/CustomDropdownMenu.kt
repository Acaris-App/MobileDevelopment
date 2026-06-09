package com.acaris.core.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.acaris.core.ui.theme.PrimaryGradient

// 1. REGULAR DROPDOWN FIELD
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

    var textFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {

            val colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )

            BasicTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                decorationBox = { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown Icon"
                            )
                        },
                        colors = colors,

                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                        container = {
                            OutlinedTextFieldDefaults.ContainerBox(
                                enabled = true,
                                isError = false,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    )
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = !expanded }
                    )
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 16),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(
                    focusable = true,
                    clippingEnabled = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                        .glowShadow(
                            color = MaterialTheme.colorScheme.primary,
                            alpha = 0.35f,
                            blurRadius = 16.dp,
                            borderRadius = 12.dp
                        )
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .heightIn(max = 240.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(options.size) { index ->
                            val option = options[index]
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
    }
}

// 2. FLOATING DROPDOWN
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
            properties = PopupProperties(focusable = true, clippingEnabled = false)
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val backgroundBrush = when {
                        isPressed || isSelected -> PrimaryGradient
                        else -> SolidColor(MaterialTheme.colorScheme.surface)
                    }

                    val contentColor = when {
                        isSelected || isPressed -> MaterialTheme.colorScheme.background
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .glowShadow(
                                color = MaterialTheme.colorScheme.primary,
                                alpha = 0.35f,
                                blurRadius = 12.dp,
                                borderRadius = 50.dp
                            )
                            .clip(CircleShape)
                            .background(backgroundBrush)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = LocalIndication.current,
                                onClick = {
                                    onOptionSelected(option)
                                    onDismissRequest()
                                }
                            )
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
                                    buttonSize = 28.dp,
                                    iconSize = 16.dp,
                                    showGlow = false,
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