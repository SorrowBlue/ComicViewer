package com.sorrowblue.comicviewer.app.navigation

import com.ramcosta.composedestinations.annotation.ExternalNavGraph
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.settings.navgraphs.SettingsNavGraph

/**
 * Main graph
 *
 * @constructor Create empty Main graph
 */
@NavHostGraph
internal annotation class MainGraph {

    @ExternalNavGraph<SettingsNavGraph>(start = true)
    @ExternalNavGraph<LibraryNavGraph>
    companion object Includes
}
