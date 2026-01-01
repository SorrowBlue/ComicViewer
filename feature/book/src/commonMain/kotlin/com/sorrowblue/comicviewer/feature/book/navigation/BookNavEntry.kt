package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.BookScreenRoot
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BookScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookNavEntry(navigator: Navigator) {
    entry<BookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) {
        with(rememberRetained { factory.createBookScreenContext() }) {
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
                    navigator.navigate<BookNavKey>(
                        BookNavKey(
                            bookshelfId = book.bookshelfId,
                            path = book.path,
                            name = book.name,
                            collectionId = collectionId,
                        ),
                        inclusive = true,
                    )
                },
                onContainerLongClick = {
                    navigator.navigate(BookMenuNavKey)
                },
            )
        }
    }
}
