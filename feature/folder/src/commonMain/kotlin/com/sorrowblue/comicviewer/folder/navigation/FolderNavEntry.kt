/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.search.nav.SearchNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.folder.FolderScreenRoot
import com.sorrowblue.comicviewer.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.framework.ui.file.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

private const val SCENE_KEY_FOLDER = "Folder"

@Serializable
internal data class FolderFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun folderNavEntry(navigator: Navigator) {
    scope.entry<FolderNavKey>(
        clazzContentKey = { it.toString() },
        metadata = metadata { transitionMaterialSharedAxisZ() } +
            SupportingPaneSceneStrategy.mainPane(SCENE_KEY_FOLDER),
    ) { navKey ->
        FolderScreenRoot(
            bookshelfId = navKey.bookshelfId,
            path = navKey.path,
            restorePath = navKey.restorePath,
            showSearch = navKey.showSearch,
            onBackClick = {
                navigator.pop<FolderNavKey>(inclusive = true)
            },
            onSearchClick = {
                navigator.navigate(SearchNavKey(navKey.bookshelfId, navKey.path))
            },
            onFileClick = { file ->
                when (file) {
                    is Book -> {
                        navigator.navigate(
                            BookNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                                name = file.name,
                            ),
                        )
                    }

                    is Folder -> {
                        navigator.popNavigate<FolderFileInfoNavKey>(
                            FolderNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                                showSearch = navKey.showSearch,
                            ),
                        )
                    }
                }
            },
            onFileInfoClick = { file ->
                navigator.popNavigate<FolderFileInfoNavKey>(
                    FolderFileInfoNavKey(file.key()),
                )
            },
            onSettingsClick = {
                navigator.navigate(SettingsNavKey)
            },
            onRestoreComplete = {
                navKey.onRestoreComplete?.invoke()
            },
        )
    }
    fileInfoEntry<FolderFileInfoNavKey>(
        sceneKey = SCENE_KEY_FOLDER,
        onBackClick = navigator::goBack,
        onCollectionClick = { file ->
            navigator.navigate(BasicCollectionAddNavKey(file.bookshelfId, file.path))
        },
        onOpenFolderClick = {},
    )
}
