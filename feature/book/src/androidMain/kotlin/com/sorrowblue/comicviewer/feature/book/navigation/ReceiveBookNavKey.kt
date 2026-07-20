/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveBookNavKey(val uri: String) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun receiveBookNavEntry(navigator: Navigator) {
    scope.entry<ReceiveBookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) {
        ReceiveBookScreenRoot(
            uri = it.uri,
            onBackClick = navigator::goBack,
        )
    }
}
