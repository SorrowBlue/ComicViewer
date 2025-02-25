package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettings
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.display.section.DisplaySettingsDarkMode
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlinx.serialization.Serializable
import logcat.logcat
import org.koin.core.annotation.Module
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Serializable
@NavGraph(startDestination = DisplaySettings::class)
data object DisplaySettingsNavGraph {

    @DestinationInGraph<DisplaySettings>
    @DestinationInGraph<DisplaySettingsDarkMode>
    object Include
}

@Module
class DisplaySettingsModule {
    @Scope(name = SettingsScope)
    @Scoped
    internal fun displaySettingsNavGraphNavigator(
        navController: NavController,
        settingsDetailNavigator: SettingsDetailNavigator,
    ): DisplaySettingsScreenNavigator =
        DisplaySettingsNavGraphNavigator(navController, settingsDetailNavigator)
}

internal class DisplaySettingsNavGraphNavigator(
    private val navController: NavController,
    private val settingsDetailNavigator: SettingsDetailNavigator,
) : DisplaySettingsScreenNavigator {

    init {
        logcat { "navController=$navController" }
    }

    override fun navigateBack() {
        settingsDetailNavigator.navigateBack()
    }

    override fun navigateToDarkMode() {
        navController.navigate(DisplaySettingsDarkMode)
    }
}
