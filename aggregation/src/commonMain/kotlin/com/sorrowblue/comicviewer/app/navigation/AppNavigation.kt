package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavKey
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationEntryGroup
import com.sorrowblue.comicviewer.feature.book.navigation.BookKey
import com.sorrowblue.comicviewer.feature.book.navigation.bookEntryGroup
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.bookshelfEntryGroup
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKey
import com.sorrowblue.comicviewer.feature.collection.navigation.collectionEntryGroup
import com.sorrowblue.comicviewer.feature.history.navigation.historyEntryGroup
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterEntryGroup
import com.sorrowblue.comicviewer.feature.search.navigation.SearchKey
import com.sorrowblue.comicviewer.feature.search.navigation.searchEntryGroup
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsKey
import com.sorrowblue.comicviewer.feature.settings.navigation.settingsEntryGroup
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialKey
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialEntryGroup
import com.sorrowblue.comicviewer.folder.navigation.sortTypeSelectEntry
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State

context(graph: PlatformGraph, appNavigationState: Navigation3State)
internal fun EntryProviderScope<NavKey>.appNavigation(
    onBookshelfFolderRestored: () -> Unit,
) {
    val onSettingsClick = {
        appNavigationState.addToBackStack(SettingsKey)
    }
    bookshelfEntryGroup(
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            appNavigationState.addToBackStack(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onBookClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onRestored = onBookshelfFolderRestored,
        onCollectionClick = { file ->
            // TODO
        },
    )
    historyEntryGroup(
        onSettingsClick = onSettingsClick,
        onBookClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onCollectionClick = {},
    )
    collectionEntryGroup(
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            appNavigationState.addToBackStack(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onCollectionBookClick = { book, collectionId ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                    collectionId = collectionId,
                ),
            )
        },
        onBookClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
    )
    readLaterEntryGroup(
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            appNavigationState.addToBackStack(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onBookClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onCollectionClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
    )

    searchEntryGroup(
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            appNavigationState.addToBackStack(
                SearchKey.List(id, path),
            )
        },
        onBookClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onCollectionClick = { book ->
            appNavigationState.addToBackStack(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onSmartCollectionClick = { id, searchCondition ->
            appNavigationState.addToBackStack(
                CollectionKey.CreateSmart(id, searchCondition),
            )
        },
    )
    settingsEntryGroup(
        onChangeAuthEnable = {
            if (it) {
                appNavigationState.addToBackStack(
                    AuthenticationNavKey(
                        ScreenType.Register,
                    ),
                )
            } else {
                appNavigationState.addToBackStack(
                    AuthenticationNavKey(
                        ScreenType.Erase,
                    ),
                )
            }
        },
        onPasswordChangeClick = {
            appNavigationState.addToBackStack(
                AuthenticationNavKey(
                    ScreenType.Change,
                ),
            )
        },
        onTutorialClick = {
            appNavigationState.addToBackStack(TutorialKey)
        },
    )
    tutorialEntryGroup()
    authenticationEntryGroup()

    sortTypeSelectEntry(
        onBackClick = {
            appNavigationState.onBackPressed()
        },
    )

    bookEntryGroup(onSettingsClick = onSettingsClick)
    extraNavigation()
}
