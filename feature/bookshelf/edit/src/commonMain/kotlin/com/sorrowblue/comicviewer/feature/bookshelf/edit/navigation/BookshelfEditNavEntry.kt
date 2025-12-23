package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditType
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BookshelfEditScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookshelfEditNavEntry(navigator: Navigator) {
    entry<BookshelfEditNavKey>(metadata = DialogSceneStrategy.dialog()) {
        with(rememberRetained { factory.createBookshelfEditScreenContext() }) {
            BookshelfEditScreenRoot(
                type = it.type,
                onBackClick = navigator::goBack,
                discardConfirm = { navigator.navigate(BookshelfDiscardConfirmNavKey) },
                onEditComplete = {
                    when (it.type) {
                        is BookshelfEditType.Edit ->
                            navigator.goBack()
                        is BookshelfEditType.Register ->
                            navigator.pop<BookshelfSelectionNavKey>(inclusive = true)
                    }
                },
            )
        }
    }
}
