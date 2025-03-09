package com.sorrowblue.comicviewer.feature.tutorial

import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Singleton

@Singleton
internal actual class AndroidSplitInstallManager {
    actual val installedModulesSet: Set<String> = setOf(DocumentModule)

    actual fun setStateListener(function: (DocumentSheetUiState) -> Unit) {
        // No implementation other than Android
    }

    actual fun registerListener() {
        // No implementation other than Android
    }

    actual fun unregisterListener() {
        // No implementation other than Android
    }

    actual suspend fun requestInstall(module: String) {
        // No implementation other than Android
    }
}
