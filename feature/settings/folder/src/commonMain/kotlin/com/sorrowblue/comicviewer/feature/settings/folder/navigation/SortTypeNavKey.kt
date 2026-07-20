/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder.SortOrderScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data class SortTypeNavKey(val sortType: SortType) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun sortTypeNavEntry(navigator: Navigator) {
    scope.entry<SortTypeNavKey>(metadata = DialogSceneStrategy.dialog()) {
        SortOrderScreenRoot(
            sortType = it.sortType,
            onDismissRequest = navigator::goBack,
        )
    }
}
