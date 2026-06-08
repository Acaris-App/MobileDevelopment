package com.acaris.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pastikan SortItem diletakkan di tempat yang bisa diakses (misal di file ini juga)
data class SortItem(val id: String, val label: String)

@Composable
fun CustomSearchAndSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchPlaceholder: String = "Cari...",
    sortOptions: List<SortItem>,
    currentSort: String,
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSortMenu by remember { mutableStateOf(false) }
    val activeSortOption = sortOptions.find { it.id == currentSort }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar dengan Glow Shadow (Persis seperti yang kita perbaiki tadi)
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .glowShadow(
                    color = MaterialTheme.colorScheme.secondary,
                    alpha = 0.35f,
                    blurRadius = 6.dp,
                    borderRadius = 20.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(50)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (searchQuery.isEmpty()) {
                    Text(searchPlaceholder, color = Color.Gray, fontSize = 14.sp)
                }
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Sort Button
        Box {
            CustomCircularIconButton(
                icon = Icons.Outlined.FilterList, // 🌟 Diubah jadi FilterList agar konsisten
                contentDescription = "Urutkan",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp),
                onClick = { expandedSortMenu = true }
            )

            CustomFloatingDropdownMenu(
                expanded = expandedSortMenu,
                onDismissRequest = { expandedSortMenu = false },
                options = sortOptions,
                selectedOption = activeSortOption,
                optionLabelProvider = { it.label },
                onOptionSelected = { selectedItem ->
                    onSortSelected(selectedItem.id)
                }
            )
        }
    }
}