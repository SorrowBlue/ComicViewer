/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data object InAppLanguagePickerNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun inAppLanguagePickerNavEntry(navigator: Navigator) {
    scope.entry<InAppLanguagePickerNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        InAppLanguagePickerScreenRoot(onBackClick = navigator::goBack)
    }
}
