package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettings
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.info.license.License
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import org.koin.core.annotation.Singleton

@Serializable
@NavGraph(startDestination = AppInfoSettings::class)
data object AppInfoSettingsNavGraph {

    @DestinationInGraph<AppInfoSettings>
    @DestinationInGraph<License>
    object Include
}

@Module
class AppInfoSettingsModule {
    @Scope(name = SettingsScope)
    @Scoped
    internal fun appInfoSettingsScreenNavigator(
        navController: NavController,
        settingsDetailNavigator: SettingsDetailNavigator,
    ): AppInfoSettingsScreenNavigator =
        AppInfoSettingsNavGraphNavigator(navController, settingsDetailNavigator)
}

@Singleton
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
