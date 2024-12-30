package com.sorrowblue.comicviewer.framework.ui.adaptive

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
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
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

// This declaration is opt-in and its usage should be marked with @androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi or @OptIn(markerClass = androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi.class)
@Composable
internal fun AnimatedExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<*>,
    extraPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = modifier
    )
}

@Composable
@PreviewMultiScreen
private fun AnimatedExtraPaneScaffoldPreview(
    @PreviewParameter(ConfigProvider::class) parameter: AnimatedExtraPaneScaffoldConfig,
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
        AnimatedExtraPaneScaffold(
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

private data class AnimatedExtraPaneScaffoldConfig(
    val item: ThreePaneScaffoldDestinationItem<String>,
    val navigation: Boolean,
    val isInvertedOrientation: Boolean,
)

private class ConfigProvider : PreviewParameterProvider<AnimatedExtraPaneScaffoldConfig> {
    override val values: Sequence<AnimatedExtraPaneScaffoldConfig> =
        sequenceOf(
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = true,
                isInvertedOrientation = false,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = true,
                isInvertedOrientation = true,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = false,
                isInvertedOrientation = false,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                navigation = false,
                isInvertedOrientation = true,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = true,
                isInvertedOrientation = false,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = true,
                isInvertedOrientation = true,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = false,
                isInvertedOrientation = false,
            ),
            AnimatedExtraPaneScaffoldConfig(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                navigation = false,
                isInvertedOrientation = true,
            ),
        )
}
