package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfDiscardConfirmScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

internal fun EntryProviderScope<NavKey>.bookshelfDiscardConfirmNavEntry(navigator: Navigator) {
    entry<BookshelfDiscardConfirmNavKey>(metadata = DialogSceneStrategy.dialog()) {
        BookshelfDiscardConfirmScreenRoot(
            onBackClick = navigator::goBack,
            onDiscard = { navigator.pop<BookshelfEditNavKey>(true) },
            onKeep = navigator::goBack,
        )
    }
}
