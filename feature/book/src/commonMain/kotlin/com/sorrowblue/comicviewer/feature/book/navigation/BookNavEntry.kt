package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BookScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookNavEntry(navigator: Navigator) {
    entry<BookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) { it ->
        with(rememberRetained { factory.createBookScreenContext() }) {
            BookScreen(
                bookshelfId = it.bookshelfId,
                path = it.path,
                name = it.name,
                collectionId = it.collectionId,
                onBackClick = navigator::goBack,
                onSettingsClick = { /* onSettingsClick */ },
                onNextBookClick = { book, collectionId ->
                    navigator.navigate(
                        BookNavKey(
                            bookshelfId = book.bookshelfId,
                            path = book.path,
                            name = book.name,
                            collectionId = collectionId,
                        )
                    )
                },
                onContainerLongClick = {
                    navigator.navigate(BookMenuNavKey)
                },
            )
        }
    }
}
