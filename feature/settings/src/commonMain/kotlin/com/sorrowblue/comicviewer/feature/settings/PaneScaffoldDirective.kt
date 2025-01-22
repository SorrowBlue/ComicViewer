package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.allVerticalHingeBounds
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.occludingVerticalHingeBounds
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Composable
internal fun <T> rememberFixListDetailPaneScaffoldNavigator(
    scaffoldDirective: PaneScaffoldDirective = calculatePaneScaffoldDirective(
        currentWindowAdaptiveInfo()
    ),
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>> = listOf(
        ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List)
    ),
) = rememberListDetailPaneScaffoldNavigator(
    scaffoldDirective = scaffoldDirective,
    initialDestinationHistory = initialDestinationHistory
)

internal fun calculateLowerInfoPaneScaffoldDirective(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    verticalHingePolicy: HingePolicy = HingePolicy.AvoidSeparating,
): PaneScaffoldDirective {
    val maxHorizontalPartitions: Int
    val horizontalPartitionSpacerSize: Dp
    when {
        windowAdaptiveInfo.windowSizeClass.containsWidthDp(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            maxHorizontalPartitions = 2
            horizontalPartitionSpacerSize = 0.dp
        }

        else -> {
            maxHorizontalPartitions = 1
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
