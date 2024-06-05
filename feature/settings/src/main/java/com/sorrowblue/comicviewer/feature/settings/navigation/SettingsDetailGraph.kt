package com.sorrowblue.comicviewer.feature.settings.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalNavGraph
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.settings.display.destinations.DisplaySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.navgraphs.FolderSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.info.navgraphs.AppInfoSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.viewer.destinations.ViewerSettingsScreenDestination

@NavHostGraph(visibility = CodeGenVisibility.INTERNAL)
internal annotation class SettingsDetailGraph {

    @ExternalDestination<DisplaySettingsScreenDestination>(start = true)
    @ExternalNavGraph<FolderSettingsNavGraph>
    @ExternalDestination<ViewerSettingsScreenDestination>
    @ExternalDestination<SecuritySettingsScreenDestination>
    @ExternalNavGraph<AppInfoSettingsNavGraph>
    companion object Includes
}
