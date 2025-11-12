package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable

@Composable
context(context: DisplaySettingsScreenContext)
fun DisplaySettingsScreenRoot(onBackClick: () -> Unit, onDarkModeClick: () -> Unit) {
    val state = rememberDisplaySettingsScreenState()
    DisplaySettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRestoreOnLaunchChange = state::onRestoreOnLaunchChange,
        onDarkModeClick = onDarkModeClick,
    )
}
