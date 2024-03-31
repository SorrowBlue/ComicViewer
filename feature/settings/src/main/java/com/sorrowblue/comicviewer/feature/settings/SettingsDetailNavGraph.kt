package com.sorrowblue.comicviewer.feature.settings

import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.feature.settings.destinations.DonationScreenDestination
import com.sorrowblue.comicviewer.feature.settings.destinations.InAppLanguagePickerScreenDestination
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination
import com.sorrowblue.comicviewer.feature.settings.info.destinations.AppInfoSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.viewer.destinations.ViewerSettingsScreenDestination

internal object SettingsDetailNavGraph : NavGraphSpec {
    override val route = "settings_detail"
    override val startRoute = DisplaySettingsScreenDestination
    override val destinationsByRoute = listOf(
        DisplaySettingsScreenDestination,
        DonationScreenDestination,
        FolderSettingsScreenDestination,
        ViewerSettingsScreenDestination,
        SecuritySettingsScreenDestination,
        AppInfoSettingsScreenDestination,
        InAppLanguagePickerScreenDestination,
        SupportExtensionScreenDestination
    ).associateBy(DestinationSpec<*>::route)
}
