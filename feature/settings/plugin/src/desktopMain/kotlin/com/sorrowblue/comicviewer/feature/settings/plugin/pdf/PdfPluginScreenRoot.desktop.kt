package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import androidx.compose.runtime.Composable

@Composable
context(context: PdfPluginScreenContext)
internal actual fun PdfPluginScreenRoot(onBackClick: () -> Unit) {
    val state = rememberPdfPluginScreenState()
    PdfPluginScreen(
        uiState = state.uiState,
        onOpenFolderClick = state::onOpenFolderClick,
    )
}
