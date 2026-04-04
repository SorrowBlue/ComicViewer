package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

internal data class PdfPluginScreenUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)
