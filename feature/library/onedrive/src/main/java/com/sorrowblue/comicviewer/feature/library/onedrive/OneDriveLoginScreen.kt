package com.sorrowblue.comicviewer.feature.library.onedrive

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveGraph
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class OneDriveLoginScreenUiState(val isRunning: Boolean = false) : Parcelable

internal interface OneDriveLoginScreenNavigator {
    fun navigateUp()
    fun onCompleted()
}

@Destination<OneDriveGraph>
@Composable
internal fun OneDriveLoginScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: OneDriveLoginScreenNavigator,
    state: OneDriveLoginScreenState =
        rememberOneDriveLoginScreenState(savedStateHandle = navBackStackEntry.savedStateHandle),
) {
    state.events.forEach { event ->
        when (event) {
            OneDriveLoginScreenEvent.Authenticated -> {
                state.consumeEvent(event)
                navigator.onCompleted()
            }
        }
    }

    val uiState = state.uiState
    OneDriveLoginScreen(
        uiState = uiState,
        onCloseClick = navigator::navigateUp,
        onLoginClick = state::onLoginClick
    )

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
}

@Composable
private fun OneDriveLoginScreen(
    uiState: OneDriveLoginScreenUiState,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.sorrowblue.comicviewer.app.R.string.onedrive_title)) },
                navigationIcon = {
                    CloseIconButton(onClick = onCloseClick)
                },
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            )
        },
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
                Text(text = stringResource(id = R.string.onedrive_action_login))
            }
        }
    }
}
