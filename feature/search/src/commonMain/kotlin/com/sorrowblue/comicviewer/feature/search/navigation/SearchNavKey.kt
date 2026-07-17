/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.search.SearchScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(val bookshelfId: BookshelfId, val path: PathString) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun searchNavEntry(navigator: Navigator) {
    scope.entry<SearchNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane("Search") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) { navKey ->
        SearchScreenRoot(
            bookshelfId = navKey.bookshelfId,
            path = navKey.path,
            onBackClick = {
                navigator.pop<SearchNavKey>(inclusive = true)
            },
            onSettingsClick = {
                navigator.navigate(SettingsNavKey)
            },
            onSmartCollectionClick = { id, condition ->
                navigator.navigate(SmartCollectionCreateNavKey(id, condition))
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
                        navigator.popNavigate<SearchFileInfoNavKey>(
                            SearchFolderNavKey(
                                file.bookshelfId,
                                file.path,
                            ),
                        )
                    }
                }
            },
            onFileInfoClick = {
                navigator.popNavigate<SearchFileInfoNavKey>(
                    SearchFileInfoNavKey(it.key()),
                )
            },
        )
    }
}
