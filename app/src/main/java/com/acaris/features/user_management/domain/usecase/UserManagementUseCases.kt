package com.acaris.features.user_management.domain.usecase

data class UserManagementUseCases(
    val getUserDetail: GetUserDetailUseCase,
    val getUsers: GetUsersUseCase,
    val addAdmin: AddAdminUseCase,
    val updateUser: UpdateUserUseCase,
    val changeUserStatus: ChangeUserStatusUseCase,
    val deleteUser: DeleteUserUseCase,
    val getMahasiswaDocuments: GetMahasiswaDocumentsUseCase,
    val getBimbinganHistory: GetBimbinganHistoryUseCase,
    val uploadMahasiswaDocument: UploadMahasiswaDocumentUseCase,
    val updateMahasiswaDocument: UpdateMahasiswaDocumentUseCase,
    val deleteMahasiswaDocument: DeleteMahasiswaDocumentUseCase
)