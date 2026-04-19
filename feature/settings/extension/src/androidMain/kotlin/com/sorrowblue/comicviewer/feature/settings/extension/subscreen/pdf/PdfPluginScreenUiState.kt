package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.pdf

internal data class PdfPluginScreenUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)
