package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun SettingsScreenRoot(onBackClick: () -> Unit, onSettingsClick: (SettingsItem) -> Unit) {
    val state = rememberSettingsScreenState()
    SettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onSettingsClick = {
            state.onSettingsClick(it, onSettingsClick)
        },
        modifier = Modifier.testTag("SettingsScreenRoot"),
    )
}
