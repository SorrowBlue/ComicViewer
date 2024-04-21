package com.sorrowblue.comicviewer.feature.library.googledrive

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.google.api.services.people.v1.PeopleServiceScopes
import com.sorrowblue.comicviewer.feature.library.googledrive.data.AuthStatus
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleAuthorizationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Stable
internal interface GoogleDriveLoginScreenState {

    val uiState: GoogleDriveLoginScreenUiState
    val events: SnapshotStateList<GoogleDriveLoginScreenEvent>

    fun onLoginClick(
        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    )

    fun onLoginResult(activityResult: ActivityResult)
    fun consumeEvent(event: GoogleDriveLoginScreenEvent)
}

@Composable
internal fun rememberGoogleDriveLoginScreenState(
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope = rememberCoroutineScope(),
    authRepository: GoogleAuthorizationRepository = koinInject(),
): GoogleDriveLoginScreenState {
    return remember {
        GoogleDriveLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            scope = scope,
            authRepository = authRepository
        )
    }
}

sealed interface GoogleDriveLoginScreenEvent {
    data object Authenticated : GoogleDriveLoginScreenEvent
}

private val scopes = listOf(
    Scope(DriveScopes.DRIVE_READONLY),
    Scope(PeopleServiceScopes.USERINFO_PROFILE)
)

@OptIn(SavedStateHandleSaveableApi::class)
private class GoogleDriveLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    private val scope: CoroutineScope,
    private val authRepository: GoogleAuthorizationRepository,
) : GoogleDriveLoginScreenState {

    override var events = mutableStateListOf<GoogleDriveLoginScreenEvent>()
        private set

    init {
        authRepository.state.filter { it == AuthStatus.Authenticated }.onEach {
            events += GoogleDriveLoginScreenEvent.Authenticated
        }.launchIn(scope)
    }

    override var uiState by savedStateHandle.saveable { mutableStateOf(GoogleDriveLoginScreenUiState()) }
        private set

    override fun consumeEvent(event: GoogleDriveLoginScreenEvent) {
        events.remove(event)
    }

    override fun onLoginClick(
        activityResultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    ) {
        scope.launch {
            authRepository.authorize(scopes, activityResultLauncher)
        }
    }

    override fun onLoginResult(activityResult: ActivityResult) {
        authRepository.authorizeResult(activityResult)
    }
}
