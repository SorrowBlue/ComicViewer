/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestScreenRoot
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class BookshelfNotificationNavKey(val scanType: ScanType) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun bookshelfNotificationNavEntry(navigator: Navigator) {
    scope.entry<BookshelfNotificationNavKey>(metadata = DialogSceneStrategy.dialog()) {
        NotificationRequestScreenRoot(
            scanType = it.scanType,
            onBackClick = navigator::goBack,
        )
    }
}
