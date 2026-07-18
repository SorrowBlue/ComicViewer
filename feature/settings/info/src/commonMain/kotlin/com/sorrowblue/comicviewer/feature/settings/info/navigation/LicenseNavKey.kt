/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
internal data object LicenseNavKey : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun pdfPluginNavEntry(navigator: Navigator) {
    scope.entry<LicenseNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisX()) {
        LicenseScreenRoot(onBackClick = dropUnlessResumed { navigator.goBack() })
    }
}
