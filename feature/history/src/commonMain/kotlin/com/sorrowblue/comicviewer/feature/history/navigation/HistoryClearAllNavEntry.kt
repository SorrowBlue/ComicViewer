package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

internal fun EntryProviderScope<NavKey>.historyClearAllNavEntry(navigator: Navigator) {
    entry<HistoryClearAllNavKey>(metadata = DialogSceneStrategy.dialog()) {
        ClearAllHistoryScreenRoot(onClose = navigator::goBack)
    }
}
