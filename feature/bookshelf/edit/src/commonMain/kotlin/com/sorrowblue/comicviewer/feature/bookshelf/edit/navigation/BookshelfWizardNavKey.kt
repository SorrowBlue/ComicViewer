/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.nav.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun bookshelfWizardNavEntry(navigator: Navigator) {
    scope.entry<BookshelfWizardNavKey.Selection>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
        ),
    ) {
        BookshelfEditScreenRoot(it, onBack = navigator::goBack)
    }
    scope.entry<BookshelfWizardNavKey.Edit>(
        metadata = DialogSceneStrategy.dialog(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
            ),
        ),
    ) {
        BookshelfEditScreenRoot(it, onBack = navigator::goBack)
    }
}
