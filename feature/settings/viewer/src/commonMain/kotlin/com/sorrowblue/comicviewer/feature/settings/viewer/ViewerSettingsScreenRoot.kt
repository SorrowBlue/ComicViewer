package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable

@Composable
context(context: ViewerSettingsScreenContext)
fun ViewerSettingsScreenRoot(onBackClick: () -> Unit) {
    val state = rememberViewerSettingsScreenState()
    ViewerSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onStatusBarShowChange = state::onStatusBarShowChange,
        onNavigationBarShowChange = state::onNavigationBarShowChange,
        onTurnOnScreenChange = state::onTurnOnScreenChange,
        onCutWhitespaceChange = state::onCutWhitespaceChange,
        onCacheImageChange = state::onCacheImageChange,
        onDisplayFirstPageChange = state::onDisplayFirstPageChange,
        onPreloadPagesChange = state::onPreloadPagesChange,
        onImageQualityChange = state::onImageQualityChange,
        onFixScreenBrightnessChange = state::onFixScreenBrightnessChange,
        onScreenBrightnessChange = state::onScreenBrightnessChange,
    )
}
