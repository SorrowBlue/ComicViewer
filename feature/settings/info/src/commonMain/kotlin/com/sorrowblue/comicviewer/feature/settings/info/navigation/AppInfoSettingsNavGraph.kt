package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettings
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.info.license.License
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Serializable
@NavGraph(startDestination = AppInfoSettings::class)
data object AppInfoSettingsNavGraph {

    @DestinationInGraph<AppInfoSettings>
    @DestinationInGraph<License>
    object Include
}

@Scope(name = SettingsScope)
@Scoped
internal class AppInfoSettingsNavGraphNavigator(
    private val navController: NavController,
    private val navigator: SettingsDetailNavigator,
) : AppInfoSettingsScreenNavigator {
    override fun navigateToLicense() {
        navController.navigate(License)
    }

    override fun navigateBack() {
        navigator.navigateBack()
    }
}
