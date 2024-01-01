package com.sorrowblue.comicviewer.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface SettingsScreenState {
    val windowAdaptiveInfo: WindowAdaptiveInfo
    val navigator: ThreePaneScaffoldNavigator
    val navController: NavHostController
    val uiState: SettingsScreenUiState
    fun onAppLanguageClick(fallback: () -> Unit)
    fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit)
    fun onDetailBackClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberSettingsScreenState(
    savedStateHandle: SavedStateHandle,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    navigator: ThreePaneScaffoldNavigator = rememberListDetailPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(windowAdaptiveInfo)
    ),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
): SettingsScreenState = remember {
    SettingsScreenStateImpl(
        savedStateHandle = savedStateHandle,
        windowAdaptiveInfo = windowAdaptiveInfo,
        navigator = navigator,
        navController = navController,
        context = context,
        scope = scope,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
@Stable
private class SettingsScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    override val windowAdaptiveInfo: WindowAdaptiveInfo,
    override val navigator: ThreePaneScaffoldNavigator,
    override val navController: NavHostController,
    private val context: Context,
    private val scope: CoroutineScope,
) : SettingsScreenState {

    override var uiState: SettingsScreenUiState by savedStateHandle.saveable {
        mutableStateOf(SettingsScreenUiState())
    }
        private set

    override fun onAppLanguageClick(fallback: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.startActivity(
                Intent(
                    Settings.ACTION_APP_LOCALE_SETTINGS,
                    Uri.parse("package:${context.applicationInfo.packageName}")
                )
            )
        } else {
            fallback()
        }
    }

    override fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit) {
        when (settings2) {
            Settings2.LANGUAGE -> {
                onAppLanguageClick { onSettingsClick2(settings2) }
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
        uiState = uiState.copy(
            listPaneUiState = uiState.listPaneUiState.copy(
                currentSettings2 = settings2
            )
        )
        scope.launch {
            delay(250)
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
    }

    override fun onDetailBackClick() {
        navigator.navigateBack()
    }
}
