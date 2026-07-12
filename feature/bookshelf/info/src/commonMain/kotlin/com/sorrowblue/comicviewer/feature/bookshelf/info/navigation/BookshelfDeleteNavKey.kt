/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class BookshelfDeleteNavKey(val id: BookshelfId) : NavKey

internal fun EntryProviderScope<NavKey>.bookshelfDeleteNavEntry(navigator: Navigator) {
    entry<BookshelfDeleteNavKey>(metadata = DialogSceneStrategy.dialog()) {
        BookshelfDeleteScreenRoot(
            bookshelfId = it.id,
            onBackClick = navigator::goBack,
            onComplete = { navigator.pop<BookshelfInfoNavKey>(true) },
        )
    }
}
