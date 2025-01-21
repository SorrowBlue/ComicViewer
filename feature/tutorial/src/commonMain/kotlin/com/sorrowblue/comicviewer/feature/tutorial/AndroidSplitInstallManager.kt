package com.sorrowblue.comicviewer.feature.tutorial

import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState

internal const val DocumentModule = "document"

internal expect class AndroidSplitInstallManager {

    val installedModulesSet: Set<String>

    fun setStateListener(function: (DocumentSheetUiState) -> Unit)

    fun registerListener()

    fun unregisterListener()

    suspend fun requestInstall(module: String)
}
