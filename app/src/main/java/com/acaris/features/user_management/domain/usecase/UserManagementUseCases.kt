package com.acaris.features.user_management.domain.usecase

data class UserManagementUseCases(
    val getUsers: GetUsersUseCase,
    val addAdmin: AddAdminUseCase,
    val updateUser: UpdateUserUseCase,
    val changeUserStatus: ChangeUserStatusUseCase,
    val deleteUser: DeleteUserUseCase
)