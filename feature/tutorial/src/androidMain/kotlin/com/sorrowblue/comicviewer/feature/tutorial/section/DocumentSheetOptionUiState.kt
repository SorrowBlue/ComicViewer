package com.sorrowblue.comicviewer.feature.tutorial.section

import com.sorrowblue.comicviewer.domain.usecase.PdfPluginState

internal data class DocumentSheetOptionUiState(
    val pluginState: PdfPluginState = PdfPluginState.NotInstalled,
)
