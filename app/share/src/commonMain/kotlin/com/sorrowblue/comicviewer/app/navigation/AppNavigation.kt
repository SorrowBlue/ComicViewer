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
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(context: PlatformContext)
internal fun EntryProviderScope<NavKey>.appNavigation(
    navigator: Navigator,
    onBookshelfFolderRestored: () -> Unit,
) {
    val onSettingsClick = {
        navigator.navigate(SettingsKey)
    }
    bookshelfEntryGroup(
        navigator = navigator,
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            navigator.navigate(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onBookClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onRestored = onBookshelfFolderRestored,
        onCollectionClick = { file ->
            navigator.navigate(CollectionKey.AddBasic(file.bookshelfId, file.path))
        },
    )
    historyEntryGroup(
        navigator = navigator,
        onSettingsClick = onSettingsClick,
        onBookClick = { book ->
            navigator.navigate(
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
        navigator = navigator,
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            navigator.navigate(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onCollectionBookClick = { book, collectionId ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                    collectionId = collectionId,
                ),
            )
        },
        onBookClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
    )
    readLaterEntryGroup(
        navigator = navigator,
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            navigator.navigate(
                SearchKey.List(
                    id,
                    path,
                ),
            )
        },
        onBookClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onCollectionClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
    )

    searchEntryGroup(
        navigator = navigator,
        onSettingsClick = onSettingsClick,
        onSearchClick = { id, path ->
            navigator.navigate(
                SearchKey.List(id, path),
            )
        },
        onBookClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onCollectionClick = { book ->
            navigator.navigate(
                BookKey(
                    bookshelfId = book.bookshelfId,
                    path = book.path,
                    name = book.name,
                ),
            )
        },
        onSmartCollectionClick = { id, searchCondition ->
            navigator.navigate(
                CollectionKey.CreateSmart(id, searchCondition),
            )
        },
    )
    settingsEntryGroup(
        navigator = navigator,
        onChangeAuthEnable = {
            if (it) {
                navigator.navigate(
                    AuthenticationNavKey(
                        ScreenType.Register,
                    ),
                )
            } else {
                navigator.navigate(
                    AuthenticationNavKey(
                        ScreenType.Erase,
                    ),
                )
            }
        },
        onPasswordChangeClick = {
            navigator.navigate(
                AuthenticationNavKey(
                    ScreenType.Change,
                ),
            )
        },
        onTutorialClick = {
            navigator.navigate(TutorialKey)
        },
    )
    tutorialEntryGroup(navigator = navigator)
    authenticationEntryGroup(navigator = navigator)

    sortTypeSelectEntry(onBackClick = navigator::goBack)

    bookEntryGroup(navigator = navigator, onSettingsClick = onSettingsClick)
    extraNavigation(navigator = navigator)
}
