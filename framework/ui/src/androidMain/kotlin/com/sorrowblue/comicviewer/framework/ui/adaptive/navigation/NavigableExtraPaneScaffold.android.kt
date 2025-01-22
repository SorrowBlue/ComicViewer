package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewConfig
import com.sorrowblue.comicviewer.framework.ui.preview.layout.scratch

@Composable
internal actual fun NavigableExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<*>,
    extraPane: @Composable () -> Unit,
    modifier: Modifier,
    defaultBackBehavior: BackNavigationBehavior,
    paneExpansionDragHandle: (@Composable ThreePaneScaffoldScope.(PaneExpansionState) -> Unit)?,
    paneExpansionState: PaneExpansionState,
    content: @Composable () -> Unit,
) {
    NavigableSupportingPaneScaffold(
        navigator = navigator,
        mainPane = {
            AnimatedPane(
                modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions != 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.End))
                } else {
                    Modifier
                }
            ) {
                content()
            }
        },
        supportingPane = {},
        extraPane = {
            AnimatedPane(
                modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions != 1 && navigator.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                } else {
                    Modifier
                }
            ) {
                extraPane()
            }
        },
        defaultBackBehavior = defaultBackBehavior,
        paneExpansionDragHandle = paneExpansionDragHandle,
        paneExpansionState = paneExpansionState,
        modifier = modifier
    )
}

@Composable
@PreviewMultiScreen
private fun NavigableExtraPaneScaffoldPreview(
    @PreviewParameter(NavigableExtraPaneScaffoldConfigProvider::class) parameter: NavigableExtraPaneScaffoldConfig,
) {
    PreviewCompliantNavigation(
        config = PreviewConfig(
            navigation = parameter.navigation,
            isInvertedOrientation = parameter.isInvertedOrientation
        )
    ) {
        val navigator = rememberSupportingPaneScaffoldNavigator<Any>(
            initialDestinationHistory = listOf(parameter.item)
        )
        NavigableExtraPaneScaffold(
            navigator = navigator,
            extraPane = {
                Scaffold(contentWindowInsets = WindowInsets.safeDrawing) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .scratch(Color.Green)
                            .padding(it)
                            .scratch(Color.Cyan)
                    )
                }
            },
        ) {
            Scaffold(contentWindowInsets = WindowInsets.safeDrawing) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .scratch(Color.Red)
                        .padding(it)
                        .scratch(Color.Blue)
                )
            }
        }
    }
}

private data class NavigableExtraPaneScaffoldConfig(
    val item: ThreePaneScaffoldDestinationItem<String>,
    val navigation: Boolean,
    val isInvertedOrientation: Boolean,
)

private class NavigableExtraPaneScaffoldConfigProvider :
    PreviewParameterProvider<NavigableExtraPaneScaffoldConfig> {
    override val values: Sequence<NavigableExtraPaneScaffoldConfig> =
        sequenceOf(
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = true,
                isInvertedOrientation = false,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = true,
                isInvertedOrientation = true,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = false,
                isInvertedOrientation = false,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = false,
                isInvertedOrientation = true,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = true,
                isInvertedOrientation = false,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = true,
                isInvertedOrientation = true,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = false,
                isInvertedOrientation = false,
            ),
            NavigableExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = false,
                isInvertedOrientation = true,
            ),
        )
}
