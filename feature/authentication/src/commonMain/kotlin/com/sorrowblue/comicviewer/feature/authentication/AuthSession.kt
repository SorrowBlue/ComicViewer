/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import logcat.logcat

sealed interface AuthState {
    data class AuthRequired(val authed: Boolean) : AuthState
    data object Loading : AuthState
    data object NoAuthRequired : AuthState
}

@SingleIn(AppScope::class)
@Inject
class AuthSession(
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
) {
    val authState: StateFlow<AuthState>
        field = MutableStateFlow<AuthState>(AuthState.Loading)
    val authRequired: StateFlow<Boolean> = manageSecuritySettingsUseCase.settings
        .map { !it.password.isNullOrEmpty() }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.Eagerly, false)

    val lockOnBackground: StateFlow<Boolean> = manageSecuritySettingsUseCase.settings
        .map { it.lockOnBackground }
        .distinctUntilChanged()
        .onEach {
            logcat { "lockOnBackground=$it" }
        }
        .stateIn(scope, SharingStarted.Eagerly, false)

    init {
        authRequired.onEach { required ->
            logcat { "authRequired=$required, authState=${authState.value}" }
            if (required) {
                authState.value = when (authState.value) {
                    is AuthState.AuthRequired -> AuthState.AuthRequired(false)
                    AuthState.Loading -> AuthState.AuthRequired(false)
                    AuthState.NoAuthRequired -> AuthState.AuthRequired(true)
                }
            } else {
                authState.value = AuthState.NoAuthRequired
            }
        }.launchIn(scope)
    }

    fun onAuthComplete() {
        authState.value = AuthState.AuthRequired(true)
    }

    fun onPause() {
        if (lockOnBackground.value && authRequired.value) {
            authState.value = AuthState.AuthRequired(false)
        }
    }
}
