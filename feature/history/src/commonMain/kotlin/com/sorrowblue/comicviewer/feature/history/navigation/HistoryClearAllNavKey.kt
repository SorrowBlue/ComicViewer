/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data object HistoryClearAllNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun historyClearAllNavEntry(navigator: Navigator) {
    scope.entry<HistoryClearAllNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ClearAllHistoryScreenRoot(onClose = navigator::goBack)
    }
}
