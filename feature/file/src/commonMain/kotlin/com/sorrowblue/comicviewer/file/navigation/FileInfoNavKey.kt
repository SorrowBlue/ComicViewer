package com.sorrowblue.comicviewer.file.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained

interface FileInfoNavKey : ScreenKey {
    val fileKey: File.Key
    val isOpenFolderEnabled: Boolean
}

context(factory: FileInfoScreenContext.Factory)
inline fun <reified T : FileInfoNavKey> EntryProviderScope<NavKey>.fileInfoEntry(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onCollectionClick: (File) -> Unit,
    noinline onOpenFolderClick: (File) -> Unit,
) {
    entry<T>(
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey) +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createFileInfoScreenContext() }) {
            FileInfoScreenRoot(
                fileKey = it.fileKey,
                isOpenFolderEnabled = it.isOpenFolderEnabled,
                onBackClick = onBackClick,
                onCollectionClick = onCollectionClick,
                onOpenFolderClick = onOpenFolderClick,
            )
        }
    }
}
