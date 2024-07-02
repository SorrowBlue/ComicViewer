package com.sorrowblue.comicviewer.feature.library.dropbox

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.library.dropbox.navigation.DropBoxGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlinx.parcelize.Parcelize

internal interface DropBoxLoginScreenNavigator {
    fun navigateUp()
    fun onLoginCompleted()
}

@Destination<DropBoxGraph>
@Composable
internal fun DropBoxLoginScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: DropBoxLoginScreenNavigator,
) {
    DropBoxLoginScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onCloseClick = navigator::navigateUp,
        onLoginComplete = navigator::onLoginCompleted,
    )
}

@Composable
private fun DropBoxLoginScreen(
    savedStateHandle: SavedStateHandle,
    onCloseClick: () -> Unit,
    onLoginComplete: () -> Unit,
    state: DropBoxLoginScreenState = rememberDropBoxLoginScreenState(savedStateHandle = savedStateHandle),
) {
    state.events.forEach { event ->
        when (event) {
            is DropBoxLoginScreenEvent.Authenticated -> {
                state.consumeEvent(event)
                onLoginComplete()
            }
        }
    }

    DropBoxLoginScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onCloseClick = onCloseClick,
        onLoginClick = state::onLoginClick,
    )
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
}

@Parcelize
internal data class DropBoxLoginScreenUiState(
    val isRunning: Boolean = false,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropBoxLoginScreen(
    uiState: DropBoxLoginScreenUiState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(com.sorrowblue.comicviewer.app.R.string.dropbox_title)) },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = ComicIcons.Close, contentDescription = "Close")
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            AnimatedVisibility(visible = uiState.isRunning) {
                LinearProgressIndicator()
            }
            Button(enabled = !uiState.isRunning, onClick = onLoginClick) {
                Text(text = stringResource(R.string.dropbox_action_login))
            }
        }
    }
}
