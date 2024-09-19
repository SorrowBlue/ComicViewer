package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme2

@Composable
fun AnimatedExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<*>,
    extraPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val defaultBackBehavior: BackNavigationBehavior =
        BackNavigationBehavior.PopUntilScaffoldValueChange
    // TODO(b/330584029): support predictive back
    BackHandler(enabled = navigator.canNavigateBack(defaultBackBehavior)) {
        navigator.navigateBack(defaultBackBehavior)
    }
    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
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
                modifier = if (navigator.scaffoldDirective.maxHorizontalPartitions != 1) {
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
    @PreviewParameter(DestinationItemProvider::class) parameter: AnimatedExtraPaneScaffoldParameter,
) {
    PreviewTheme2(showNavigation = parameter.showNavigation) {
        val navigator = rememberSupportingPaneScaffoldNavigator<String>(
            initialDestinationHistory = listOf(parameter.item)
        )
        AnimatedExtraPaneScaffold(
            navigator = navigator,
            extraPane = {
                ScratchBox(
                    color = Color.Green,
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
        ) {
            ScratchBox(
                color = Color.Cyan,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

data class AnimatedExtraPaneScaffoldParameter(
    val item: ThreePaneScaffoldDestinationItem<String>,
    val showNavigation: Boolean,
)

private class DestinationItemProvider :
    PreviewParameterProvider<AnimatedExtraPaneScaffoldParameter> {
    override val values: Sequence<AnimatedExtraPaneScaffoldParameter> =
        sequenceOf(
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Main
                ),
                true
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Main
                ),
                false
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    "extra"
                ),
                true
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(
                    SupportingPaneScaffoldRole.Extra,
                    "extra"
                ),
                false
            ),
        )
}

@Composable
fun ScratchBox(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        inset {
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color,
                Offset(0f, 0f),
                Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color,
                Offset(size.width, 0f),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color,
                Offset(0f, size.height),
                Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
            val max = Math.round(size.width / 100)
            with(density) {
                for (i in 0..max) {
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(size.width, size.height / max * (max - i)),
                        strokeWidth = strokeWidth.toPx()
                    )
                    drawLine(
                        color.copy(alpha = 0.5f),
                        Offset(size.width / max * i, 0f),
                        Offset(0f, size.height / max * i),
                        strokeWidth = strokeWidth.toPx()
                    )
                    if (i > 0) {
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(0f, size.height / max * i),
                            Offset(size.width / max * (max - i), size.height),
                            strokeWidth = strokeWidth.toPx()
                        )
                        drawLine(
                            color.copy(alpha = 0.5f),
                            Offset(size.width / max * i, size.height),
                            Offset(size.width, (size.height / max) * i),
                            strokeWidth = strokeWidth.toPx()
                        )
                    }
                }
            }
        }
    }
}
