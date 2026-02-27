package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfWizardScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: BookshelfEditScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookshelfWizardNavEntry(navigator: Navigator) {
    entry<BookshelfWizardNavKey.Selection>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
        ),
    ) {
        with(rememberRetained { factory.createBookshelfEditScreenContext() }) {
            BookshelfWizardScreenRoot(it, onBack = navigator::goBack)
        }
    }
    entry<BookshelfWizardNavKey.Edit>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
        ),
    ) {
        with(rememberRetained { factory.createBookshelfEditScreenContext() }) {
            BookshelfWizardScreenRoot(it, onBack = navigator::goBack)
        }
    }
}
