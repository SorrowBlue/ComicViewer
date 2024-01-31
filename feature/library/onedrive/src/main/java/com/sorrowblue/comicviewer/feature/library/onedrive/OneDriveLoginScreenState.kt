package com.sorrowblue.comicviewer.feature.library.onedrive

import android.content.Context
import androidx.activity.ComponentActivity
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
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal sealed interface OneDriveLoginScreenEvent {
    data object Authenticated : OneDriveLoginScreenEvent
}

@Stable
internal interface OneDriveLoginScreenState {
    val events: SnapshotStateList<OneDriveLoginScreenEvent>
    val uiState: OneDriveLoginScreenUiState
    fun onLoginClick()
    fun onResume()
    fun consumeEvent(event: OneDriveLoginScreenEvent)
}

@Composable
internal fun rememberOneDriveLoginScreenState(
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    repository: OneDriveApiRepository = koinInject(),
): OneDriveLoginScreenState {
    return remember {
        OneDriveLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            scope = scope,
            activity = context as ComponentActivity,
            repository = repository
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class OneDriveLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    private val scope: CoroutineScope,
    private val activity: ComponentActivity,
    private val repository: OneDriveApiRepository,
) : OneDriveLoginScreenState {

    override var uiState: OneDriveLoginScreenUiState by savedStateHandle
        .saveable { mutableStateOf(OneDriveLoginScreenUiState()) }
        private set

    override var events = mutableStateListOf<OneDriveLoginScreenEvent>()
        private set

    init {
        scope.launch {
            repository.initialize()
            repository.accountFlow.collectLatest { account ->
                if (account != null) {
                    events += OneDriveLoginScreenEvent.Authenticated
                }
            }
        }
    }

    override fun consumeEvent(event: OneDriveLoginScreenEvent) {
        events.remove(event)
    }

    override fun onLoginClick() {
        scope.launch {
            repository.login(activity)
        }
    }

    override fun onResume() {
        repository.loadAccount()
    }
}
