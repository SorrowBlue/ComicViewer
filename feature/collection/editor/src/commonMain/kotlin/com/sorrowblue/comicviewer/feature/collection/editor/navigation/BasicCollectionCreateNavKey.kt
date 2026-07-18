/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class BasicCollectionCreateNavKey(
    val bookshelfId: BookshelfId = BookshelfId.Companion(),
    val path: PathString = "",
) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun basicCollectionCreateNavEntry(navigator: Navigator) {
    scope.entry<BasicCollectionCreateNavKey>(
        metadata = DialogSceneStrategy.dialog(DialogProperties(usePlatformDefaultWidth = false)),
    ) {
        BasicCollectionCreateScreenRoot(
            bookshelfId = it.bookshelfId,
            path = it.path,
            onBackClick = navigator::goBack,
            onComplete = navigator::goBack,
        )
    }
}
