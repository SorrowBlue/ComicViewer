package com.sorrowblue.comicviewer.feature.library.box

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
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import com.sorrowblue.comicviewer.framework.ui.component.CloseIconButton
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BoxLoginScreenUiState(val isRunning: Boolean = false) : Parcelable

@Destination
@Composable
internal fun BoxLoginScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: CoreNavigator,
) {
    BoxLoginScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onCloseClick = navigator::navigateUp,
    )
}

@Composable
private fun BoxLoginScreen(
    savedStateHandle: SavedStateHandle,
    onCloseClick: () -> Unit,
    state: BoxLoginScreenState = rememberBoxLoginScreenState(savedStateHandle = savedStateHandle),
) {
    val uiState = state.uiState
    BoxLoginScreen(
        uiState = uiState,
        onCloseClick = onCloseClick,
        onLoginClick = state::onLoginClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxLoginScreen(
    uiState: BoxLoginScreenUiState,
    onCloseClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.box_title2)) },
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
                Text(text = stringResource(R.string.box_action_login))
            }
        }
    }
}
