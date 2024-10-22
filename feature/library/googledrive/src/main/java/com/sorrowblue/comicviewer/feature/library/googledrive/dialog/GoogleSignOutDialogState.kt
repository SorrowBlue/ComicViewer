package com.sorrowblue.comicviewer.feature.library.googledrive.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleAuthorizationRepository
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal interface GoogleSignOutDialogState {
    val uiState: GoogleSignOutDialogUiState
    fun onSignOutClick(onSignOut: () -> Unit)
}

@Composable
internal fun rememberGoogleSignOutDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: GoogleDriveApiRepository = koinInject(),
    authorizationRepository: GoogleAuthorizationRepository = koinInject(),
): GoogleSignOutDialogState {
    return remember {
        GoogleSignOutDialogStateImpl(
            repository = repository,
            scope = scope,
            authorizationRepository = authorizationRepository
        )
    }
}

private class GoogleSignOutDialogStateImpl(
    repository: GoogleDriveApiRepository,
    private val scope: CoroutineScope,
    private val authorizationRepository: GoogleAuthorizationRepository,
) : GoogleSignOutDialogState {
    override var uiState by mutableStateOf(GoogleSignOutDialogUiState())
        private set

    init {
        repository.profile.filterNotNull().onEach {
            uiState = uiState.copy(
                photoUrl = it.photos.firstOrNull()?.url.orEmpty(),
                name = it.names.firstOrNull()?.displayName.orEmpty()
            )
        }.launchIn(scope)
    }

    override fun onSignOutClick(onSignOut: () -> Unit) {
        scope.launch {
            authorizationRepository.signOut()
            onSignOut()
        }
    }
}
