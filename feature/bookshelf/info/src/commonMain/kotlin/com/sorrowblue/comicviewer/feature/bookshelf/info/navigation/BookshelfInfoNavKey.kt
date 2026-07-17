/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfInfoNavKey(val id: BookshelfId) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun bookshelfInfoNavEntry(navigator: Navigator) {
    scope.entry<BookshelfInfoNavKey>(
        metadata = SupportingPaneSceneStrategy.extraPane(sceneKey = "Bookshelf") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        BookshelfInfoScreenRoot(
            bookshelfId = it.id,
            onBackClick = navigator::goBack,
            onRemoveClick = {
                navigator.navigate(BookshelfDeleteNavKey(it.id))
            },
            showNotificationPermissionRationale = { scanType ->
                navigator.navigate(BookshelfNotificationNavKey(scanType))
            },
            onEditClick = { id, type ->
                navigator.navigate(BookshelfWizardNavKey.Edit(id, type))
            },
        )
    }
}
