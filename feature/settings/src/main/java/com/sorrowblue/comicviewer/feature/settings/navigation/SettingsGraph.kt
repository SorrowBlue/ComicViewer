package com.sorrowblue.comicviewer.feature.settings.navigation

import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination

@NavGraph<ExternalModuleGraph>
internal annotation class SettingsGraph {

    @ExternalDestination<AuthenticationScreenDestination>
    companion object Includes
}
