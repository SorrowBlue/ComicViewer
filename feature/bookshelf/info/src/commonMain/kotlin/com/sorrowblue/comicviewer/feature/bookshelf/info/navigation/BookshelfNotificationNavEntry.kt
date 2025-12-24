package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

internal fun EntryProviderScope<NavKey>.bookshelfNotificationNavEntry(navigator: Navigator) {
    entry<BookshelfNotificationNavKey>(metadata = DialogSceneStrategy.dialog()) {
        NotificationRequestScreenRoot(
            scanType = it.scanType,
            onBackClick = navigator::goBack,
        )
    }
}
