/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.DialogSceneStrategy.Companion.DialogKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.nav.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.permission.nav.localNetworkPermission
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator

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
        BookshelfEditScreenRoot(
            key = it,
            onBack = navigator::goBack,
        )
    }
    scope.entry<BookshelfWizardNavKey.Edit>(
        metadata = { key ->
            metadata {
                put(DialogKey, DialogProperties(usePlatformDefaultWidth = false))
                if (key.bookshelfType == BookshelfType.SMB) {
                    localNetworkPermission()
                }
            }
        },
    ) {
        BookshelfEditScreenRoot(
            key = it,
            onBack = navigator::goBack,
        )
    }
}
