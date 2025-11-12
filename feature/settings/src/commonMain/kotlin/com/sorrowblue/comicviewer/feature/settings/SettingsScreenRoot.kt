package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsScreenRoot(
    onBackClick: () -> Unit,
    onSettingsClick: (SettingsItem) -> Unit,
    onSettingsLongClick: (SettingsItem) -> Unit,
) {
    val state = rememberSettingsScreenState()
    SettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onSettingsClick = {
            state.onSettingsClick(it, onSettingsClick)
        },
        onSettingsLongClick = onSettingsLongClick,
    )
}
