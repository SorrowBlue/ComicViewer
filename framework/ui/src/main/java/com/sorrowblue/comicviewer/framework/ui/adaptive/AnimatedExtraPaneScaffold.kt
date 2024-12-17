package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import com.sorrowblue.comicviewer.framework.ui.preview.DeviceTemplate
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.ScratchBox
import com.sorrowblue.comicviewer.framework.ui.preview.nextLoremIpsum

@Composable
fun AnimatedExtraPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
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
    PreviewTheme {
        DeviceTemplate(template = DeviceTemplate(showSystemBar = parameter.systemBar)) {
            val navigationState =
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo())
            CompliantNavigationSuiteScaffold(
                navigationSuiteItems = {
                    repeat(5) {
                        item(
                            selected = it == 0,
                            onClick = {},
                            icon = { Icon(ComicIcons.Folder, null) },
                            label = { Text(nextLoremIpsum().take(8)) }

                        )
                    }
                },
                navigationState = if (parameter.navigation) {
                    navigationState
                } else {
                    when (navigationState) {
                        is NavigationState.NavigationBar -> navigationState.copy(false)
                        is NavigationState.NavigationDrawer -> navigationState.copy(false)
                        is NavigationState.NavigationRail -> navigationState.copy(false)
                    }
                }
            ) {
                val navigator = rememberSupportingPaneScaffoldNavigator<Any>(
                    initialDestinationHistory = listOf(parameter.item)
                )
                AnimatedExtraPaneScaffold(
                    navigator = navigator,
                    extraPane = {
                        Scaffold(
                            contentWindowInsets = WindowInsets.safeDrawing
                        ) {
                            ScratchBox(
                                color = Color.Blue.copy(0.5f),
                                modifier = Modifier
                                    .padding(it)
                                    .fillMaxSize()
                            )
                        }
                    },
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing
                    ) {
                        ScratchBox(
                            color = Color.Red.copy(0.5f),
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

data class AnimatedExtraPaneScaffoldParameter(
    val item: ThreePaneScaffoldDestinationItem<String>,
    val systemBar: Boolean,
    val navigation: Boolean,
)

private class DestinationItemProvider :
    PreviewParameterProvider<AnimatedExtraPaneScaffoldParameter> {
    override val values: Sequence<AnimatedExtraPaneScaffoldParameter> =
        sequenceOf(
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                systemBar = true,
                navigation = true,
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main),
                systemBar = true,
                navigation = false,
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                systemBar = true,
                navigation = true,
            ),
            AnimatedExtraPaneScaffoldParameter(
                ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Extra, "extra"),
                systemBar = true,
                navigation = false,
            ),
        )
}
