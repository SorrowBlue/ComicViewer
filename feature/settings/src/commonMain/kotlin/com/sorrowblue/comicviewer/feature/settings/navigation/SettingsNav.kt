package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.info.navigation.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(factory: DisplaySettingsScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.settingsNavEntry(navigator: Navigator) {
    entry<SettingsNavKey>(
        metadata = ListDetailSceneStrategy.listPane(
            "Settings",
            detailPlaceholder = {
                with(factory.createDisplaySettingsScreenContext()) {
                    DisplaySettingsScreenRoot(
                        onBackClick = navigator::goBack,
                        onDarkModeClick = {},
                    )
                }
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
                    SettingsItem.EXTENSION -> TODO()
                    SettingsItem.HELP -> navigator.navigate(InfoSettingsNavKey)
                }
            },
        )
    }
}
