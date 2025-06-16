package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavController
import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.Destination
import com.sorrowblue.cmpdestinations.GraphNavigation
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePicker
import com.sorrowblue.comicviewer.feature.settings.SettingsItem
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCache
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettings
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettings
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@NavGraph(
    startDestination = DisplaySettingsNavGraph::class,
    destinations = [
        ImageCache::class,
        SecuritySettings::class,
        ViewerSettings::class,
        InAppLanguagePicker::class,
    ],
    nestedGraphs = [
        AppInfoSettingsNavGraph::class,
        DisplaySettingsNavGraph::class,
        FolderSettingsNavGraph::class,
    ]
)
expect object SettingsDetailNavGraph : GraphNavigation {
    override val destinations: Array<Destination>
    override val nestedGraphs: Array<GraphNavigation>
    override val route: KClass<*>
    override val startDestination: KClass<*>
    override val transitions: NavTransitions
    override val typeMap: Map<KType, NavType<*>>
}

@Scope(SettingsScope::class)
@Scoped(binds = [SecuritySettingsScreenNavigator::class, SettingsDetailNavigator::class, SettingsExtraNavigator::class])
internal class SettingsDetailNavGraphNavigator(
    private val scope: CoroutineScope,
    private val navController: NavController,
    private val navigator: ThreePaneScaffoldNavigator<SettingsItem>,
    private val settingsScreenNavigator: SettingsScreenNavigator,
) : SecuritySettingsScreenNavigator, SettingsDetailNavigator, SettingsExtraNavigator {

    override fun navigateToChangeAuth(enabled: Boolean) {
        settingsScreenNavigator.navigateToChangeAuth(enabled)
    }

    override fun navigateToPasswordChange() {
        settingsScreenNavigator.onPasswordChange()
    }

    override fun navigateBack() {
        scope.launch {
            navigator.navigateBack()
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
