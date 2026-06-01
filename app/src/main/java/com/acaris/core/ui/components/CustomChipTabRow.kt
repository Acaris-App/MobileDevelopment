package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomChipTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Jarak antar tab
        contentPadding = PaddingValues(vertical = 4.dp) // Padding atas bawah di luar chip
    ) {
        itemsIndexed(tabs) { index, title ->
            val isSelected = index == selectedTabIndex

            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.Transparent
            val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                    .clickable { onTabSelected(index) }
                    // 🌟 FIX: Padding internal dikurangi agar chip lebih ramping
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = contentColor,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    // 🌟 FIX: Ukuran font dikecilkan sedikit agar serasi dengan padding yang baru
                    fontSize = 13.sp
                )
            }
        }
    }
}