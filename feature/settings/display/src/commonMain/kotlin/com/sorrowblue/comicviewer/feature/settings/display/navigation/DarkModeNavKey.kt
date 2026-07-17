/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.settings.display.darkmode.DarkModeScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data object DarkModeNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun darkModeNavEntry(navigator: Navigator) {
    scope.entry<DarkModeNavKey>(metadata = DialogSceneStrategy.dialog()) {
        DarkModeScreenRoot(
            onDismissRequest = dropUnlessResumed { navigator.goBack() },
            onComplete = dropUnlessResumed { navigator.goBack() },
        )
    }
}
