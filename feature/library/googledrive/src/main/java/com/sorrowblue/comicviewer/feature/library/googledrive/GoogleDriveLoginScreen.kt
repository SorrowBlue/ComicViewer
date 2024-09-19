package com.sorrowblue.comicviewer.feature.library.googledrive

import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.library.googledrive.navigation.GoogleDriveGraph
import com.sorrowblue.comicviewer.framework.ui.material3.CloseIconButton
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class GoogleDriveLoginScreenUiState(val isRunning: Boolean = false) : Parcelable

interface GoogleDriveLoginScreenNavigator {
    fun navigateUp()
    fun onComplete()
}

@Destination<GoogleDriveGraph>
@Composable
internal fun GoogleDriveLoginScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: GoogleDriveLoginScreenNavigator,
) {
    GoogleDriveLoginScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onCloseClick = navigator::navigateUp,
        onComplete = navigator::onComplete,
    )
}

@Composable
private fun GoogleDriveLoginScreen(
    savedStateHandle: SavedStateHandle,
    onCloseClick: () -> Unit,
    onComplete: () -> Unit,
    state: GoogleDriveLoginScreenState = rememberGoogleDriveLoginScreenState(savedStateHandle = savedStateHandle),
) {
    state.events.forEach {
        when (it) {
            is GoogleDriveLoginScreenEvent.Authenticated -> onComplete()
        }
    }
    val uiState = state.uiState

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = state::onLoginResult
    )

    GoogleDriveLoginScreen(
        uiState = uiState,
        onCloseClick = onCloseClick,
        onLoginClick = { state.onLoginClick(activityResultLauncher) }
    )
}

@Composable
private fun GoogleDriveLoginScreen(
    uiState: GoogleDriveLoginScreenUiState,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = com.sorrowblue.comicviewer.app.R.string.googledrive_title))
                },
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
                Text(text = stringResource(R.string.googledrive_action_login))
            }
        }
    }
}
