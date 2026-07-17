/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data object BookMenuNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun bookMenuNavEntry(navigator: Navigator) {
    scope.entry<BookMenuNavKey>(metadata = DialogSceneStrategy.dialog()) {
        BookMenuScreenRoot(onDismissRequest = navigator::goBack)
    }
}
