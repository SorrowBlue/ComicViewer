package com.sorrowblue.comicviewer.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberFixListDetailPaneScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Calculate lower info pane scaffold directive
 *
 * @param windowAdaptiveInfo
 * @param verticalHingePolicy
 * @return PaneScaffoldDirective
 * @see
 *    [androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective]
 */
fun calculateLowerInfoPaneScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating,
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val horizontalPartitionSpacerSize: Dp
    when (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            maxHorizontalPartitions = 1
            horizontalPartitionSpacerSize = 0.dp
        }

        WindowWidthSizeClass.MEDIUM -> {
            maxHorizontalPartitions = 2
            horizontalPartitionSpacerSize = 0.dp
        }
        else -> {
            maxHorizontalPartitions = 2
            horizontalPartitionSpacerSize = 0.dp
        }
    }
    val maxVerticalPartitions: Int
    val verticalPartitionSpacerSize: Dp

    if (windowAdaptiveInfo.windowPosture.isTabletop) {
        maxVerticalPartitions = 2
        verticalPartitionSpacerSize = 24.dp
    } else {
        maxVerticalPartitions = 1
        verticalPartitionSpacerSize = 0.dp
    }

    val defaultPanePreferredWidth = 360.dp

    return PaneScaffoldDirective(
        maxHorizontalPartitions,
        horizontalPartitionSpacerSize,
        maxVerticalPartitions,
        verticalPartitionSpacerSize,
        defaultPanePreferredWidth,
        getExcludedVerticalBounds(
            windowAdaptiveInfo.windowPosture,
            verticalHingePolicy
        )
    )
}

private fun getExcludedVerticalBounds(posture: Posture, hingePolicy: HingePolicy): List<Rect> {
    return when (hingePolicy) {
        HingePolicy.AvoidSeparating -> posture.separatingVerticalHingeBounds
        HingePolicy.AvoidOccluding -> posture.occludingVerticalHingeBounds
        HingePolicy.AlwaysAvoid -> posture.allVerticalHingeBounds
        else -> emptyList()
    }
}

internal interface SettingsScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator<Settings2>
    val navController: NavHostController
    fun onSettingsClick(settings2: Settings2, onStartTutorialClick: () -> Unit)
    fun onDetailBackClick()
}

@Composable
internal fun rememberSettingsScreenState(
    scaffoldDirective: PaneScaffoldDirective =
        calculateLowerInfoPaneScaffoldDirective(currentWindowAdaptiveInfo()),
    navigator: ThreePaneScaffoldNavigator<Settings2> = rememberFixListDetailPaneScaffoldNavigator(
        scaffoldDirective = scaffoldDirective,
    ),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
): SettingsScreenState = rememberSaveableScreenState {
    SettingsScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        navController = navController,
        context = context,
        scope = scope,
    )
}

@Stable
private class SettingsScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
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
            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, settings2)
        }
    }

    override fun onDetailBackClick() {
        scope.launch {
            navigator.navigateBack()
        }
    }
}
