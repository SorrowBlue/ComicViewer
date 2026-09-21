/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.folder.FolderScreenRoot
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.feature.permission.nav.localNetworkPermission
import com.sorrowblue.comicviewer.feature.search.nav.SearchNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ

internal const val SCENE_KEY_FOLDER = "Folder"

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun folderNavEntry(navigator: Navigator) {
    scope.entry<FolderNavKey>(
        metadata = { navKey ->
            metadata {
                transitionMaterialSharedAxisZ()
                localNetworkPermission(navKey.bookshelfId)
            } + SupportingPaneSceneStrategy.mainPane(SCENE_KEY_FOLDER)
        },
    ) { navKey ->
        FolderScreenRoot(
            bookshelfId = navKey.bookshelfId,
            path = navKey.path,
            restorePath = navKey.restorePath,
            showSearch = navKey.showSearch,
            onBackClick = dropUnlessResumed {
                navigator.pop<FolderNavKey>(inclusive = true)
            },
            onSearchClick = dropUnlessResumed {
                navigator.navigate(SearchNavKey(navKey.bookshelfId, navKey.path))
            },
            onFileClick = dropUnlessResumed { file ->
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
            onFileInfoClick = dropUnlessResumed { file ->
                navigator.popNavigate<FolderFileInfoNavKey>(
                    FolderFileInfoNavKey(file.key()),
                )
            },
            onSettingsClick = dropUnlessResumed {
                navigator.navigate(SettingsNavKey)
            },
            onRestoreComplete = {
                navKey.onRestoreComplete?.invoke()
            },
        )
    }
}
