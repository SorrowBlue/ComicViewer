package com.sorrowblue.comicviewer.feature.tutorial.section

data class DocumentSheetUiState(
    val folderPath: String = "",
    val checking: Boolean = false,
    val error: String = "",
    val info: String = "",
)
