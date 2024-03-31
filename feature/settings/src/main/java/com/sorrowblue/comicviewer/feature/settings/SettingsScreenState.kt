package com.sorrowblue.comicviewer.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface SettingsScreenState : SaveableScreenState {
    val windowAdaptiveInfo: WindowAdaptiveInfo
    val navigator: ThreePaneScaffoldNavigator<Settings2>
    val navController: NavHostController
    fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit)
    fun onDetailBackClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberSettingsScreenState(
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    navigator: ThreePaneScaffoldNavigator<Settings2> = rememberListDetailPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(windowAdaptiveInfo)
    ),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
): SettingsScreenState = rememberSaveableScreenState {
    SettingsScreenStateImpl(
        savedStateHandle = it,
        windowAdaptiveInfo = windowAdaptiveInfo,
        navigator = navigator,
        navController = navController,
        context = context,
        scope = scope,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Stable
private class SettingsScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val windowAdaptiveInfo: WindowAdaptiveInfo,
    override val navigator: ThreePaneScaffoldNavigator<Settings2>,
    override val navController: NavHostController,
    private val context: Context,
    private val scope: CoroutineScope,
) : SettingsScreenState {

    override fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit) {
        when (settings2) {
            Settings2.LANGUAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    runCatching {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APP_LOCALE_SETTINGS,
                                Uri.parse("package:${context.applicationInfo.packageName}")
                            )
                        )
                    }.onFailure {
                        onSettingsClick2(settings2)
                    }
                } else {
                    onSettingsClick2(settings2)
                }
            }

            Settings2.TUTORIAL -> {
                onStartTutorialClick()
            }

            else -> {
                onSettingsClick2(settings2)
            }
        }
    }

    private fun onSettingsClick2(settings2: Settings2) {
        scope.launch {
            delay(250)
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, settings2)
        }
    }

    override fun onDetailBackClick() {
        navigator.navigateBack()
    }
}
