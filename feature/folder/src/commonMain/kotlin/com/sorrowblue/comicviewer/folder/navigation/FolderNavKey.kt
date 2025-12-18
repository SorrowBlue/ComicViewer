package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.folder.FolderScreenRoot
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained

interface FolderNavKey : ScreenKey {
    val bookshelfId: BookshelfId
    val path: String
    val restorePath: String?
    val showSearch: Boolean get() = false
    val onRestoreComplete: (() -> Unit)? get() = null
}

context(factory: FolderScreenContext.Factory)
inline fun <reified T : FolderNavKey> EntryProviderScope<NavKey>.folderEntry(
    sceneKey: String,
    noinline onBackClick: () -> Unit,
    noinline onSearchClick: (BookshelfId, PathString) -> Unit = { _, _ -> },
    noinline onFileClick: (File) -> Unit,
    noinline onFileInfoClick: (File) -> Unit,
    noinline onSettingsClick: () -> Unit,
) {
    entry<T>(
        clazzContentKey = { it.toString() },
        metadata = SupportingPaneSceneStrategy.mainPane(sceneKey) +
            NavigationResultMetadata.resultConsumer(SortTypeSelectScreenResultKey) +
            NavDisplay.transitionMaterialSharedAxisZ(),
    ) {
        with(rememberRetained { factory.createFolderScreenContext() }) {
            FolderScreenRoot(
                bookshelfId = it.bookshelfId,
                path = it.path,
                restorePath = it.restorePath,
                showSearch = it.showSearch,
                onBackClick = onBackClick,
                onSearchClick = { onSearchClick(it.bookshelfId, it.path) },
                onFileClick = onFileClick,
                onFileInfoClick = onFileInfoClick,
                onSettingsClick = onSettingsClick,
                onRestoreComplete = {
                    it.onRestoreComplete?.invoke()
                },
            )
        }
    }
}

context(factoryFolder: FolderScreenContext.Factory, factoryFileInfo: FileInfoScreenContext.Factory)
inline fun <reified T : FolderNavKey, reified V : FileInfoNavKey> EntryProviderScope<NavKey>.folderFileInfoNavEntry(
    sceneKeyPrefix: String,
    noinline onBackClick: () -> Unit,
    noinline onSearchClick: (BookshelfId, PathString) -> Unit = { _, _ -> },
    noinline onFileClick: (File) -> Unit,
    noinline onFileInfoClick: (File) -> Unit,
    noinline onSettingsClick: () -> Unit,
    noinline onCollectionClick: (File) -> Unit,
    noinline onOpenFolderClick: (File) -> Unit = {},
) {
    folderEntry<T>(
        "${sceneKeyPrefix}Folder",
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        onSettingsClick = onSettingsClick,
    )
    fileInfoEntry<V>(
        "${sceneKeyPrefix}Folder",
        onBackClick = onBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick,
    )
}
