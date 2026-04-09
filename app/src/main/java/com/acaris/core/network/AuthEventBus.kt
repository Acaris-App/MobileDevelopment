package com.acaris.core.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun emitLogoutEvent() {
        _events.emit(AuthEvent.SessionExpired)
    }
}

sealed class AuthEvent {
    object SessionExpired : AuthEvent()
}