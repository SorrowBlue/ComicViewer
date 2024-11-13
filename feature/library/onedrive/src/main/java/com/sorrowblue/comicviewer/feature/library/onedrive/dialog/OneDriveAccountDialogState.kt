package com.sorrowblue.comicviewer.feature.library.onedrive.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun rememberOneDriveAccountDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: OneDriveApiRepository = koinInject(),
): OneDriveAccountDialogState = remember {
    OneDriveAccountDialogStateImpl(scope = scope, repository = repository)
}

internal interface OneDriveAccountDialogState {
    fun onSignOutClick(onComplete: () -> Unit)

    val uiState: OneDriveDialogUiState
}

private class OneDriveAccountDialogStateImpl(
    private val scope: CoroutineScope,
    private val repository: OneDriveApiRepository,
) : OneDriveAccountDialogState {

    override var uiState by mutableStateOf(OneDriveDialogUiState())
        private set

    init {
        repository.accountFlow.filterNotNull().onEach {
            uiState = uiState.copy(name = it.username, photoUrl = repository::profileImage)
        }.launchIn(scope)
    }

    override fun onSignOutClick(onComplete: () -> Unit) {
        scope.launch {
            repository.logout()
            onComplete()
        }
    }
}
