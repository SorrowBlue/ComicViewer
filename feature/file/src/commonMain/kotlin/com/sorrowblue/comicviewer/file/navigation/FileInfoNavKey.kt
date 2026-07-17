/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.FileInfoScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX

interface FileInfoNavKey : NavKey {
    val fileKey: File.Key
    val isOpenFolderEnabled: Boolean
}

context(scope: EntryProviderScope<NavKey>)
inline fun <reified T : FileInfoNavKey> fileInfoEntry(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onCollectionClick: (File) -> Unit,
    noinline onOpenFolderClick: (File) -> Unit,
) {
    scope.entry<T>(
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey) +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        FileInfoScreenRoot(
            fileKey = it.fileKey,
            isOpenFolderEnabled = it.isOpenFolderEnabled,
            onBackClick = onBackClick,
            onCollectionClick = onCollectionClick,
            onOpenFolderClick = onOpenFolderClick,
        )
    }
}
