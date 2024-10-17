package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComponentColors
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.containerColor
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.contentColor
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Composable
fun PreviewTheme(isInvertedOrientation: Boolean = false, content: @Composable () -> Unit) {
    EdgeToEdgeTemplate(
        navMode = NavigationMode.Gesture,
        cameraCutoutMode = CameraCutoutMode.Middle,
        showInsetsBorder = false,
        isStatusBarVisible = true,
        isNavigationBarVisible = true,
        isInvertedOrientation = isInvertedOrientation,
    ) {
        ComicTheme {
            val dimension =
                when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> compactDimension
                    WindowWidthSizeClass.MEDIUM -> mediumDimension
                    WindowWidthSizeClass.EXPANDED -> expandedDimension
                    else -> compactDimension
                }
            CompositionLocalProvider(
                LocalDimension provides dimension,
                LocalComponentColors provides ComponentColors(
                    ComicTheme.colorScheme.surface,
                    ComicTheme.colorScheme.surfaceContainer
                )
            ) {
                content()
            }
        }
    }
}

data class EdgeToEdgeTemplate(
    val navMode: NavigationMode = NavigationMode.Gesture,
    val cameraCutoutMode: CameraCutoutMode = CameraCutoutMode.Middle,
    val isInvertedOrientation: Boolean = false,
    val showInsetsBorder: Boolean = false,
    val isStatusBarVisible: Boolean = true,
    val isNavigationBarVisible: Boolean = true,
)

@Composable
fun PreviewTheme2(
    modifier: Modifier = Modifier,
    showDeviceFrame: Boolean = true,
    showNavigation: Boolean = true,
    template: EdgeToEdgeTemplate = EdgeToEdgeTemplate(),
    content: @Composable () -> Unit,
) {
    val contentContent = remember { movableContentOf(content) }
    Box(
        modifier = modifier,
    ) {
        if (showDeviceFrame) {
            ComicTheme {
                EdgeToEdgeTemplate(
                    navMode = template.navMode,
                    cameraCutoutMode = template.cameraCutoutMode,
                    showInsetsBorder = template.showInsetsBorder,
                    isStatusBarVisible = template.isStatusBarVisible,
                    isNavigationBarVisible = template.isNavigationBarVisible,
                    isInvertedOrientation = template.isInvertedOrientation,
                ) {
                    val navigationState = NavigationSuiteScaffoldDefaults
                        .calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo(), showNavigation)
                    CompliantNavigationSuiteScaffold(
                        navigationSuiteItems = {
                            repeat(5) {
                                item(it == 0, {}, { Icon(ComicIcons.Folder, null) })
                            }
                        },
                        navigationState = navigationState
                    ) {
                        contentContent()
                    }
                }
            }
        } else {
            val navigationState =
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo())
            ComicTheme {
                val componentColors = ComponentColors(
                    containerColor = navigationState.containerColor(),
                    contentColor = navigationState.contentColor()
                )
                CompositionLocalProvider(
                    LocalComponentColors provides componentColors,
                    LocalNavigationState provides navigationState
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun PreviewTheme3(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        ComicTheme {
            val dimension =
                when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
                    WindowWidthSizeClass.COMPACT -> compactDimension
                    WindowWidthSizeClass.MEDIUM -> mediumDimension
                    WindowWidthSizeClass.EXPANDED -> expandedDimension
                    else -> compactDimension
                }

            CompositionLocalProvider(
                LocalDimension provides dimension,
                LocalComponentColors provides ComponentColors(
                    ComicTheme.colorScheme.surface,
                    ComicTheme.colorScheme.surfaceContainer
                )
            ) {
                CompliantNavigationSuiteScaffold({
                    repeat(5) {
                        item(it == 0, {}, { Icon(ComicIcons.Folder, null) })
                    }
                }) {
                    content()
                }
            }
        }
    }
}
