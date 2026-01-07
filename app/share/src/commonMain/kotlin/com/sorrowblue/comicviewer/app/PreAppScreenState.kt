package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface AuthStatus {
    data object Unknown : AuthStatus

    data class AuthRequired(val authed: Boolean) : AuthStatus

    data object NoAuthRequired : AuthStatus
}

@Scope
annotation class RootScreenWrapperScope

@Composable
context(context: PreAppScreenContext)
internal fun rememberPreAppScreenState(): PreAppScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        PreAppScreenStateImpl(
            scope = coroutineScope,
            loadSettingsUseCase = context.loadSettingsUseCase,
            manageSecuritySettingsUseCase = context.manageSecuritySettingsUseCase,
        )
    }
}

internal interface PreAppScreenState {
    val authStatus: AuthStatus
    val tutorialRequired: Boolean

    fun onAuthComplete()

    fun onStop()

    fun onTutorialComplete()
}

@Suppress("OPT_IN_USAGE")
private class PreAppScreenStateImpl(
    private val scope: CoroutineScope,
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : PreAppScreenState {
    override var authStatus by mutableStateOf<AuthStatus>(AuthStatus.Unknown)
        private set

    override var tutorialRequired by mutableStateOf(true)
        private set

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
                            (authStatus as AuthStatus.AuthRequired).authed
                    )
                } else {
                    AuthStatus.NoAuthRequired
                }
            }.launchIn(scope)
    }

    override fun onAuthComplete() {
        authStatus = AuthStatus.AuthRequired(authed = true)
    }

    override fun onStop() {
        scope.launch {
            if (authStatus is AuthStatus.AuthRequired &&
                manageSecuritySettingsUseCase.settings.first().lockOnBackground
            ) {
                authStatus = AuthStatus.AuthRequired(authed = false)
            }
        }
    }

    override fun onTutorialComplete() {
        scope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }
}
