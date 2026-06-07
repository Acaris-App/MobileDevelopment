package com.acaris.core.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.theme.DisabledPrimaryGradient
import com.acaris.core.ui.theme.PrimaryGradient

@Composable
fun CustomChipTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(tabs) { index, title ->
            val isSelected = index == selectedTabIndex

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            val backgroundBrush = when {
                isSelected -> PrimaryGradient
                isPressed && !isSelected -> DisabledPrimaryGradient
                else -> SolidColor(Color.Transparent)
            }

            val contentColor = when {
                isSelected || isPressed -> MaterialTheme.colorScheme.background
                else -> MaterialTheme.colorScheme.onSurface
            }

            val borderColor = if (isSelected || isPressed) MaterialTheme.colorScheme.tertiary else Color.Gray

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundBrush)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                    // 🌟 3. Masukkan interactionSource ke dalam clickable
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current, // Mempertahankan efek ripple bawaan (gelombang air)
                        onClick = { onTabSelected(index) }
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = contentColor,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp
                )
            }
        }
    }
}