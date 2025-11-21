package com.sorrowblue.comicviewer.feature.settings.plugin

import androidx.compose.runtime.Composable

@Composable
context(context: PluginScreenContext)
internal fun PluginScreenRoot(onBackClick: () -> Unit, onPdfPluginClick: () -> Unit) {
    val state = rememberPluginScreenState()
    PluginScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onPdfPluginClick = onPdfPluginClick,
    )
}
