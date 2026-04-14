package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuthStatus {
    data object Unknown : AuthStatus

    data class AuthRequired(val authed: Boolean) : AuthStatus

    data object NoAuthRequired : AuthStatus
}

@Composable
context(context: PreAppScreenContext)
internal fun rememberPreAppScreenState(): PreAppScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = remember {
        PreAppScreenStateImpl(
            scope = coroutineScope,
            loadSettingsUseCase = context.loadSettingsUseCase,
            manageSecuritySettingsUseCase = context.manageSecuritySettingsUseCase,
        )
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE, onEvent = state::onPause)
    return state
}

internal interface PreAppScreenState {
    val authStatus: AuthStatus
    val tutorialRequired: Boolean

    fun onAuthComplete()
    fun onTutorialComplete()
}

@OptIn(ExperimentalCoroutinesApi::class)
private class PreAppScreenStateImpl(
    private val scope: CoroutineScope,
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : PreAppScreenState {
    override var authStatus by mutableStateOf<AuthStatus>(AuthStatus.Unknown)
        private set

    override var tutorialRequired by mutableStateOf(false)
        private set

    private val lockOnBackground = manageSecuritySettingsUseCase.settings
        .map { it.lockOnBackground }
        .stateIn(scope, SharingStarted.Eagerly, false)

    init {
        // Initialize tutorial status and listen for changes
        loadSettingsUseCase.settings
            .mapLatest { !it.doneTutorial }
            .distinctUntilChanged()
            .onEach { tutorialRequired = it }
            .launchIn(scope)

        // Initialize auth status and listen for changes
        manageSecuritySettingsUseCase.settings
            .mapLatest { !it.password.isNullOrEmpty() }
            .distinctUntilChanged()
            .onEach { hasPassword ->
                authStatus = if (hasPassword) {
                    AuthStatus.AuthRequired(
                        authed =
                            authStatus is AuthStatus.AuthRequired &&
                                (authStatus as AuthStatus.AuthRequired).authed,
                    )
                } else {
                    AuthStatus.NoAuthRequired
                }
            }.launchIn(scope)
    }

    override fun onAuthComplete() {
        authStatus = AuthStatus.AuthRequired(authed = true)
    }

    override fun onTutorialComplete() {
        scope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }

    fun onPause() {
        if (authStatus is AuthStatus.AuthRequired && lockOnBackground.value) {
            authStatus = AuthStatus.AuthRequired(authed = false)
        }
    }
}
