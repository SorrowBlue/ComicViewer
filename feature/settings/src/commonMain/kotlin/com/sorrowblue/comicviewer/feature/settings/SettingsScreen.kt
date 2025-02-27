package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.NavGraphNavHost
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsScope
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCache
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsNavGraph
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsDetailNavGraphImpl
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsDetailNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.section.SettingsListPane
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettings
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettings
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigableListDetailPaneScaffold
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_label_app
import comicviewer.feature.settings.generated.resources.settings_label_display
import comicviewer.feature.settings.generated.resources.settings_label_folder
import comicviewer.feature.settings.generated.resources.settings_label_image_cache
import comicviewer.feature.settings.generated.resources.settings_label_language
import comicviewer.feature.settings.generated.resources.settings_label_security
import comicviewer.feature.settings.generated.resources.settings_label_tutorial
import comicviewer.feature.settings.generated.resources.settings_label_viewer
import kotlin.reflect.KClass
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.koinInject
import org.koin.compose.module.rememberKoinModules
import org.koin.compose.scope.KoinScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal interface SettingsScreenNavigator {
    fun navigateUp()
    fun onStartTutorialClick()
    fun navigateToChangeAuth(enabled: Boolean)
    fun onPasswordChange()
}

@Serializable
data object Settings

@OptIn(KoinExperimentalAPI::class)
@Destination<Settings>
@Composable
internal fun SettingsScreen(
    screenNavigator: SettingsScreenNavigator = koinInject(),
    state: SettingsScreenState = rememberSettingsScreenState(),
) {
    SettingsScreen(
        navigator = state.navigator,
        onBackClick = screenNavigator::navigateUp,
        onSettingsClick = { state.onSettingsClick(it, screenNavigator::onStartTutorialClick) },
    ) {
        val scope = rememberCoroutineScope()
        rememberKoinModules {
            listOf(
                module {
                    scope(named(SettingsScope)) {
                        scoped { { scope.launch { state.navigator.navigateBack() } } }
                        scoped { state.navController } bind NavController::class
                        scoped { SettingsDetailNavGraphNavigator(get(), get()) } binds arrayOf(
                            SecuritySettingsScreenNavigator::class,
                            SettingsDetailNavigator::class,
                            SettingsExtraNavigator::class,
                        )
                    }
                }
            )
        }
        val navGraph = remember { SettingsDetailNavGraphImpl() }
        KoinScope("ScopeId", named(SettingsScope)) {
            NavGraphNavHost(
                navController = state.navController,
                navGraph = navGraph,
                startDestination = state.navigator.currentDestination?.contentKey?.route
                    ?: navGraph.startDestination,
            )
        }
    }
}

@Composable
private fun SettingsScreen(
    navigator: ThreePaneScaffoldNavigator<Settings2>,
    onBackClick: () -> Unit,
    onSettingsClick: (Settings2) -> Unit,
    content: @Composable () -> Unit,
) {
    NavigableListDetailPaneScaffold(
        navigator = navigator,
        detailPane = {
            val modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions == 1) {
                Modifier
            } else {
                Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            }
            AnimatedPane(modifier = modifier) {
                content()
            }
        },
        listPane = {
            val startPadding = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LocalLayoutDirection.current)
            AnimatedPane(Modifier.preferredWidth(360.dp + startPadding)) {
                SettingsListPane(
                    navigator = navigator,
                    onBackClick = onBackClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        },
    )
}

enum class Settings2(
    val title: StringResource,
    val icon: ImageVector,
    val route: KClass<*>? = null,
) {
    DISPLAY(
        Res.string.settings_label_display,
        ComicIcons.DisplaySettings,
        DisplaySettingsNavGraph::class
    ),
    FOLDER(Res.string.settings_label_folder, ComicIcons.FolderOpen, FolderSettingsNavGraph::class),
    VIEWER(Res.string.settings_label_viewer, ComicIcons.Image, ViewerSettings::class),
    SECURITY(Res.string.settings_label_security, ComicIcons.Lock, SecuritySettings::class),
    APP(Res.string.settings_label_app, ComicIcons.Info, AppInfoSettingsNavGraph::class),
    TUTORIAL(Res.string.settings_label_tutorial, ComicIcons.Start),

    //    Donation(R.string.settings_label_donation, ComicIcons.Money, DonationScreenDestination),
    Thumbnail(Res.string.settings_label_image_cache, ComicIcons.Storage, ImageCache::class),
    LANGUAGE(Res.string.settings_label_language, ComicIcons.Language, InAppLanguagePicker::class),
}
