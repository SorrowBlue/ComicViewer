/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPlaceholder
import com.sorrowblue.comicviewer.feature.settings.nav.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.ExtensionSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun settingsNavEntry(
    navigator: Navigator,
    settingsDetailPlaceholder: SettingsDetailPlaceholder,
) {
    scope.entry<SettingsNavKey>(
        metadata = ListDetailSceneStrategy.listPane(
            "Settings",
            detailPlaceholder = {
                settingsDetailPlaceholder(navigator)
            },
        ) + NavDisplay.transitionMaterialFadeThrough(),
    ) {
        SettingsScreenRoot(
            onBackClick = dropUnlessResumed {
                navigator.pop<SettingsNavKey>(true)
            },
            onSettingsClick = dropUnlessResumed { item ->
                when (item) {
                    SettingsItem.DISPLAY -> navigator.navigate(DisplaySettingsNavKey)
                    SettingsItem.FOLDER -> navigator.navigate(FolderSettingsNavKey)
                    SettingsItem.VIEWER -> navigator.navigate(ViewerSettingsNavKey)
                    SettingsItem.SECURITY -> navigator.navigate(SecuritySettingsNavKey)
                    SettingsItem.LANGUAGE -> navigator.navigate(InAppLanguagePickerNavKey)
                    SettingsItem.EXTENSION -> navigator.navigate(ExtensionSettingsNavKey)
                    SettingsItem.HELP -> navigator.navigate(InfoSettingsNavKey)
                }
            },
        )
    }
}
