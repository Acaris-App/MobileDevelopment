package com.acaris.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acaris.features.auth.domain.usecase.LoginUseCase
import com.acaris.features.auth.presentation.mapper.toPresentation
import com.acaris.features.auth.presentation.model.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = loginUseCase(email, password)

            result.fold(
                onSuccess = { userDomain ->
                    val userPresentation = userDomain.toPresentation()
                    _loginState.value = LoginState.Success(userPresentation)
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(
                        message = exception.message ?: "Terjadi kesalahan sistem"
                    )
                }
            )
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}