package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.display.di.DisplaySettingsModule
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.folder.di.FolderSettingsModule
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCache
import com.sorrowblue.comicviewer.feature.settings.info.di.AppInfoSettingsModule
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettings
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettings
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Module

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect class SettingsDetailNavGraphImpl() :
    com.sorrowblue.cmpdestinations.NavGraph {
    override val startDestination: KClass<*>
    override val route: KClass<*>
    override val typeMap: Map<KType, NavType<*>>
    override val screenDestinations: List<ScreenDestination>
    override val nestedNavGraphs: List<com.sorrowblue.cmpdestinations.NavGraph>
    override val transitions: NavTransitions
}

@Serializable
@NavGraph(
    startDestination = DisplaySettingsNavGraph::class,
    root = SettingsDetailNavGraphImpl::class
)
internal data object SettingsDetailNavGraph {

    @NestedNavGraph<AppInfoSettingsNavGraph>
    @NestedNavGraph<DisplaySettingsNavGraph>
    @NestedNavGraph<FolderSettingsNavGraph>
    @DestinationInGraph<ImageCache>
    @DestinationInGraph<SecuritySettings>
    @DestinationInGraph<ViewerSettings>
    object Include
}

@Module(includes = [DisplaySettingsModule::class, FolderSettingsModule::class, AppInfoSettingsModule::class])
class SettingsModule

internal class SettingsDetailNavGraphNavigator(
    private val navigateBack: () -> Unit,
    private val settingsScreenNavigator: SettingsScreenNavigator,
) : SecuritySettingsScreenNavigator, SettingsDetailNavigator, SettingsExtraNavigator {

    override fun navigateToChangeAuth(enabled: Boolean) {
        settingsScreenNavigator.navigateToChangeAuth(enabled)
    }

    override fun navigateToPasswordChange() {
        settingsScreenNavigator.onPasswordChange()
    }

    override fun navigateBack() {
        navigateBack.invoke()
    }

    override fun navigateUp() {
        settingsScreenNavigator.navigateUp()
    }
}
