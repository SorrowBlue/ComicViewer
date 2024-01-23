package com.sorrowblue.comicviewer.feature.library.googledrive

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.compose.koinInject

@Stable
internal interface GoogleDriveLoginScreenState {

    val uiState: GoogleDriveLoginScreenUiState
    val events: SnapshotStateList<GoogleDriveLoginScreenEvent>

    fun onLoginClick(
        activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    )

    fun onLoginResult(activityResult: ActivityResult)
    fun consumeEvent(event: GoogleDriveLoginScreenEvent)
}

@Composable
internal fun rememberGoogleDriveLoginScreenState(
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope = rememberCoroutineScope(),
    activity: ComponentActivity = LocalContext.current as ComponentActivity,
    repository: GoogleDriveApiRepository = koinInject(),
): GoogleDriveLoginScreenState {
    return remember {
        GoogleDriveLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            scope = scope,
            activity = activity,
            repository = repository
        )
    }
}

sealed interface GoogleDriveLoginScreenEvent {
    data object Authenticated : GoogleDriveLoginScreenEvent
}

@OptIn(SavedStateHandleSaveableApi::class)
private class GoogleDriveLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope,
    private val activity: ComponentActivity,
    private val repository: GoogleDriveApiRepository,
) : GoogleDriveLoginScreenState {

    override var events = mutableStateListOf<GoogleDriveLoginScreenEvent>()
        private set

    init {
        repository.googleSignInAccount.onEach {
            if (it != null) {
                events += GoogleDriveLoginScreenEvent.Authenticated
            }
        }.launchIn(scope)
    }

    override var uiState by savedStateHandle.saveable { mutableStateOf(GoogleDriveLoginScreenUiState()) }
        private set

    override fun consumeEvent(event: GoogleDriveLoginScreenEvent) {
        events.remove(event)
    }

    override fun onLoginClick(
        activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    ) {
        repository.startSignIn(activity, activityResultLauncher)
    }

    override fun onLoginResult(activityResult: ActivityResult) {
        repository.signInResult(activityResult) {
            repository.updateAccount()
        }
    }
}
