/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.BookScreenRoot
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun bookNavEntry(navigator: Navigator) {
    scope.entry<BookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) {
        BookScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            name = it.name,
            collectionId = it.collectionId,
            onBackClick = {
                navigator.pop<BookNavKey>(inclusive = true)
            },
            onSettingsClick = { navigator.navigate(SettingsNavKey) },
            onNextBookClick = { book, collectionId ->
                navigator.popNavigate<BookNavKey>(
                    BookNavKey(
                        bookshelfId = book.bookshelfId,
                        path = book.path,
                        name = book.name,
                        collectionId = collectionId,
                    ),
                )
            },
            onContainerLongClick = {
                navigator.navigate(BookMenuNavKey)
            },
        )
    }
}
