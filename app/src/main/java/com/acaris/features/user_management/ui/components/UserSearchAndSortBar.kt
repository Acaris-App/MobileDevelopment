package com.acaris.features.user_management.ui.components

import androidx.compose.foundation.background
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

@Composable
fun UserSearchAndSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentRole: String,
    onSortSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSortMenu by remember { mutableStateOf(false) }

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

            DropdownMenu(
                expanded = expandedSortMenu,
                onDismissRequest = { expandedSortMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(text = { Text("Nama (A-Z)") }, onClick = { onSortSelected("name_asc"); expandedSortMenu = false })
                DropdownMenuItem(text = { Text("Nama (Z-A)") }, onClick = { onSortSelected("name_desc"); expandedSortMenu = false })
                DropdownMenuItem(text = { Text("NPM/NIP (Kecil ke Besar)") }, onClick = { onSortSelected("identifier_asc"); expandedSortMenu = false })

                if (currentRole == "mahasiswa") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    DropdownMenuItem(text = { Text("Angkatan (Tua ke Muda)") }, onClick = { onSortSelected("angkatan_asc"); expandedSortMenu = false })
                    DropdownMenuItem(text = { Text("Angkatan (Muda ke Tua)") }, onClick = { onSortSelected("angkatan_desc"); expandedSortMenu = false })
                    DropdownMenuItem(text = { Text("Semester (Kecil ke Besar)") }, onClick = { onSortSelected("semester_asc"); expandedSortMenu = false })
                    DropdownMenuItem(text = { Text("Semester (Besar ke Kecil)") }, onClick = { onSortSelected("semester_desc"); expandedSortMenu = false })
                }
            }
        }
    }
}