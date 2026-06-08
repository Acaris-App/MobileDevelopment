package com.acaris.features.user_management.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.acaris.core.ui.components.CustomChipTabRow
import com.acaris.core.ui.components.CustomDialog
import com.acaris.core.ui.components.CustomLoadingOverlay
import com.acaris.core.ui.components.CustomSearchAndSortBar // 🌟 MENGGUNAKAN KOMPONEN GLOBAL
import com.acaris.core.ui.components.SortItem // 🌟 MENGGUNAKAN KOMPONEN GLOBAL
import com.acaris.features.user_management.presentation.model.UserUiModel
import com.acaris.features.user_management.presentation.viewmodel.UserManagementViewModel
import com.acaris.features.user_management.ui.components.UserItemCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserManagementScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToAddAdmin: () -> Unit,
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val tabs = listOf("mahasiswa", "dosen", "admin")
    val selectedTabIndex = tabs.indexOf(state.currentRole).coerceAtLeast(0)

    var searchQuery by remember { mutableStateOf(state.currentSearch) }
    LaunchedEffect(searchQuery) {
        delay(500L)
        if (searchQuery != state.currentSearch) {
            viewModel.setSearchQuery(searchQuery)
            viewModel.loadUsers(isRefresh = true)
        }
    }

    var userToDelete by remember { mutableStateOf<UserUiModel?>(null) }
    var userToToggleStatus by remember { mutableStateOf<Pair<UserUiModel, Boolean>?>(null) }

    val listState = rememberLazyListState()
    var isScrollingUp by remember { mutableStateOf(true) }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.isLastPage && !state.isLoading && !state.isAppending) {
            viewModel.loadNextPage()
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < -10f) {
                    isScrollingUp = false
                } else if (delta > 10f) {
                    isScrollingUp = true
                }
                return Offset.Zero
            }
        }
    }

    val isAtTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    val showTabRow = isScrollingUp || isAtTop

    if (userToDelete != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { userToDelete = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hapus Pengguna", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Apakah Anda yakin ingin menghapus pengguna ini? Tindakan ini tidak dapat dibatalkan.", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Hapus",
            onConfirm = {
                userToDelete?.let {
                    viewModel.deleteUser(it.id, if (it.isActive) "active" else "inactive")
                }
                userToDelete = null
            },
            dismissText = "Batal",
            onDismiss = { userToDelete = null }
        )
    }

    if (userToToggleStatus != null) {
        val (user, targetStatus) = userToToggleStatus!!
        val actionText = if (targetStatus) "mengaktifkan" else "menonaktifkan"
        val titleText = if (targetStatus) "Aktifkan Akun" else "Nonaktifkan Akun"
        val titleColor = if (targetStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

        CustomDialog(
            showDialog = true,
            onDismissRequest = { userToToggleStatus = null },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(titleText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = titleColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Apakah Anda yakin ingin $actionText akun ${user.name}?", textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Ya, Lanjutkan",
            onConfirm = {
                viewModel.changeUserStatus(user.id, targetStatus)
                userToToggleStatus = null
            },
            dismissText = "Batal",
            onDismiss = { userToToggleStatus = null }
        )
    }

    if (state.successMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Berhasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.successMessage ?: "", textAlign = TextAlign.Center, color = Color.DarkGray)
                }
            },
            confirmText = "OK",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    if (state.errorMessage != null) {
        CustomDialog(
            showDialog = true,
            onDismissRequest = { viewModel.clearMessages() },
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.error, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Terjadi Kesalahan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.errorMessage ?: "", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = Color.Gray)
                }
            },
            confirmText = "Tutup",
            onConfirm = { viewModel.clearMessages() }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (state.currentRole == "admin") {
                FloatingActionButton(
                    onClick = onNavigateToAddAdmin,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Tambah Admin")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(nestedScrollConnection)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp)
            ) {
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            val sortOptions = remember(state.currentRole) {
                                val baseOptions = listOf(
                                    SortItem("name_asc", "Nama (A-Z)"),
                                    SortItem("name_desc", "Nama (Z-A)"),
                                    SortItem("identifier_asc", "NPM/NIP (Kecil ke Besar)")
                                )
                                if (state.currentRole == "mahasiswa") {
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

                            CustomSearchAndSortBar(
                                searchQuery = searchQuery,
                                onSearchQueryChange = { searchQuery = it },
                                searchPlaceholder = "Cari Nama, Email, atau NPM/NIP...",
                                sortOptions = sortOptions,
                                currentSort = state.currentSortBy,
                                onSortSelected = { viewModel.setSortByAndLoad(it) }
                            )

                            AnimatedVisibility(
                                visible = showTabRow,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                CustomChipTabRow(
                                    tabs = tabs.map { it.replaceFirstChar { char -> char.uppercase() } },
                                    selectedTabIndex = selectedTabIndex,
                                    onTabSelected = {
                                        viewModel.setFilterAndLoad(tabs[it])
                                        searchQuery = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (!state.isLoading && state.users.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("Tidak ada data pengguna.", color = Color.Gray)
                        }
                    }
                } else {
                    items(state.users) { user ->
                        UserItemCard(
                            user = user,
                            onCardClick = { onNavigateToDetail(user.id) },
                            onEditClick = { onNavigateToEdit(it.id) },
                            onDeleteClick = { userToDelete = it },
                            onStatusToggle = { uiModel, isActive ->
                                userToToggleStatus = Pair(uiModel, isActive)
                            },
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    if (state.isAppending) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }

            if (state.isLoading || state.isActionLoading) {
                CustomLoadingOverlay(isLoading = true)
            }
        }
    }
}