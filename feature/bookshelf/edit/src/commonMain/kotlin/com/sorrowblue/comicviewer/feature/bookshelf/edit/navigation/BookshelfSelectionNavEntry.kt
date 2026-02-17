package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.selection.BookshelfSelectionScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

internal fun EntryProviderScope<NavKey>.bookshelfSelectionNavEntry(navigator: Navigator) {
    entry<BookshelfSelectionNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        BookshelfSelectionScreenRoot(
            onBackClick = navigator::goBack,
            onTypeClick = { type ->
                navigator.pop<BookshelfSelectionNavKey>(true)
                navigator.navigate(
                    BookshelfEditNavKey(BookshelfEditType.Register(type)),
                )
            },
        )
    }
}
