package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel

sealed interface AuthStatus {
    data object Unknown : AuthStatus
    data class AuthRequired(val authed: Boolean) : AuthStatus
    data object NoAuthRequired : AuthStatus
}

@Composable
internal fun rememberRootScreenWrapperState(
    viewModel: RootScreenWrapperViewModel = koinViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
): RootScreenWrapperState {
    return remember {
        RootScreenWrapperStateImpl(
            scope = scope,
            loadSettingsUseCase = viewModel.loadSettingsUseCase,
            manageSecuritySettingsUseCase = viewModel.manageSecuritySettingsUseCase
        )
    }
}

internal interface RootScreenWrapperState {
    val authStatus: AuthStatus
    val tutorialRequired: Boolean
    fun onAuthComplete()
    fun onStop()
    fun onTutorialComplete()
}

@Suppress("OPT_IN_USAGE")
private class RootScreenWrapperStateImpl(
    val scope: CoroutineScope,
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : RootScreenWrapperState {

    override var authStatus by mutableStateOf<AuthStatus>(AuthStatus.Unknown)
        private set

    override var tutorialRequired by mutableStateOf(runBlocking { !loadSettingsUseCase.settings.first().doneTutorial })
        private set

    init {
        loadSettingsUseCase.settings.mapLatest { it.doneTutorial }.distinctUntilChanged().onEach {
            tutorialRequired = !it
        }.launchIn(scope)
        manageSecuritySettingsUseCase.settings.mapLatest { !it.password.isNullOrEmpty() }
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    authStatus = AuthStatus.AuthRequired(authed = authStatus !is AuthStatus.Unknown)
                } else {
                    authStatus = AuthStatus.NoAuthRequired
                }
            }.launchIn(scope)
    }

    override fun onAuthComplete() {
        authStatus = AuthStatus.AuthRequired(authed = true)
    }

    override fun onStop() {
        runBlocking {
            if (authStatus is AuthStatus.AuthRequired && manageSecuritySettingsUseCase.settings.first().lockOnBackground) {
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

@KoinViewModel
internal class RootScreenWrapperViewModel(
    val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel()
