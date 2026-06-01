package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acaris.core.ui.components.CustomCircularIconButton
import com.acaris.core.ui.components.CustomFloatingDropdownMenu

data class SortItem(val id: String, val label: String)

@Composable
fun UserSearchAndSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentRole: String,
    currentSort: String, // 🌟 FIX 1: Menambahkan parameter untuk menerima status urutan aktif
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSortMenu by remember { mutableStateOf(false) }

    val sortOptions = remember(currentRole) {
        val baseOptions = listOf(
            SortItem("name_asc", "Nama (A-Z)"),
            SortItem("name_desc", "Nama (Z-A)"),
            SortItem("identifier_asc", "NPM/NIP (Kecil ke Besar)")
        )
        if (currentRole == "mahasiswa") {
            baseOptions + listOf(
                SortItem("angkatan_asc", "Angkatan (Tua ke Muda)"),
                SortItem("angkatan_desc", "Angkatan (Muda ke Tua)"),
                SortItem("semester_asc", "Semester (Kecil ke Besar)"),
                SortItem("semester_desc", "Semester (Besar ke Kecil)")
            )
        } else {
            baseOptions
        }
    }

    // 🌟 FIX 2: Mencari objek SortItem yang cocok dengan currentSort String
    val activeSortOption = sortOptions.find { it.id == currentSort }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (searchQuery.isEmpty()) {
                    Text("Search", color = Color.Gray, fontSize = 14.sp)
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

        Box {
            CustomCircularIconButton(
                icon = Icons.Outlined.Sort,
                contentDescription = "Urutkan",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(40.dp),
                onClick = { expandedSortMenu = true }
            )

            CustomFloatingDropdownMenu(
                expanded = expandedSortMenu,
                onDismissRequest = { expandedSortMenu = false },
                options = sortOptions,
                // 🌟 FIX 3: Masukkan objek SortItem yang aktif ke sini!
                selectedOption = activeSortOption,
                optionLabelProvider = { it.label },
                onOptionSelected = { selectedItem ->
                    onSortSelected(selectedItem.id)
                }
            )
        }
    }
}