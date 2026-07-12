/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.folder.FolderScreenRoot
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer

interface FolderNavKey : NavKey {
    val bookshelfId: BookshelfId
    val path: String
    val restorePath: String?
    val showSearch: Boolean get() = false
    val onRestoreComplete: (() -> Unit)? get() = null
}

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
        metadata = metadata {
            put(
                NavigationResultMetadata.ResultConsumerKey,
                NavigationResultMetadata.resultConsumer(SortTypeSelectScreenResultKey),
            )
            transitionMaterialSharedAxisZ()
        } + SupportingPaneSceneStrategy.mainPane(sceneKey),
    ) {
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

inline fun <reified T : FolderNavKey, reified V : FileInfoNavKey> EntryProviderScope<NavKey>.folderFileInfoNavEntry(
    sceneKeyPrefix: String,
    noinline onBackClick: () -> Unit,
    noinline onInfoBackClick: () -> Unit,
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
        onBackClick = onInfoBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick,
    )
}
