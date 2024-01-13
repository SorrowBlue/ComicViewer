package com.sorrowblue.comicviewer.feature.library.dropbox

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepository
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

abstract class StateEvent {
    val uuid: UUID = UUID.randomUUID()

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DropBoxLoginScreenEvent
        return uuid == other.uuid
    }
}

internal sealed class DropBoxLoginScreenEvent {

    val uuid: UUID = UUID.randomUUID()

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DropBoxLoginScreenEvent
        return uuid == other.uuid
    }

    class Authenticated : DropBoxLoginScreenEvent()
}

internal interface DropBoxLoginScreenState {
    val uiState: DropBoxLoginScreenUiState
    val events: List<DropBoxLoginScreenEvent>
    val snackbarHostState: SnackbarHostState
    fun consumeEvent(event: DropBoxLoginScreenEvent)
    fun onLoginClick()
    fun onResume()
}

@Composable
internal fun rememberDropBoxLoginScreenState(
    savedStateHandle: SavedStateHandle,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    repository: DropBoxApiRepository = koinInject(),
): DropBoxLoginScreenState {
    return remember {
        DropBoxLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            snackbarHostState = snackbarHostState,
            scope = scope,
            context = context,
            repository = repository,
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class DropBoxLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    private val context: Context,
    private val repository: DropBoxApiRepository,
) : DropBoxLoginScreenState {

    init {
        repository.accountFlow.onEach {
            if (it != null) {
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.dropbox_message_auth))
                }
                delay(1000)
                events += DropBoxLoginScreenEvent.Authenticated()
            }
        }.launchIn(scope)
    }

    override var uiState: DropBoxLoginScreenUiState by savedStateHandle
        .saveable { mutableStateOf(DropBoxLoginScreenUiState()) }
        private set

    override var events by mutableStateOf(emptyList<DropBoxLoginScreenEvent>())
        private set

    override fun onLoginClick() {
        uiState = uiState.copy(isRunning = true)
        repository.startSignIn()
    }

    override fun consumeEvent(event: DropBoxLoginScreenEvent) {
        events -= event
    }

    override fun onResume() {
        if (uiState.isRunning) {
            scope.launch {
                if (!repository.dbxCredential()) {
                    uiState = uiState.copy(isRunning = false)
                    snackbarHostState.showSnackbar(context.getString(R.string.dropbox_message_not_auth))
                }
            }
        }
    }
}
