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
import androidx.compose.ui.platform.LocalContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComponentColors
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.containerColor
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.contentColor
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    ComicTheme {
        val navigationState =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo())
        val componentColors = ComponentColors(
            containerColor = navigationState.containerColor(),
            contentColor = navigationState.contentColor()
        )
        val context = LocalContext.current
        val previewHandler = AsyncImagePreviewHandler {
            PreviewImage(context)
        }
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides previewHandler,
            LocalNavigationState provides navigationState,
            LocalComponentColors provides componentColors
        ) {
            content()
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

@OptIn(ExperimentalCoilApi::class)
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
                    statusBarMode = if (template.isStatusBarVisible) InsetMode.Visible else InsetMode.Hidden,
                    navigationBarMode = if (template.isNavigationBarVisible) InsetMode.Visible else InsetMode.Hidden,
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
                        val context = LocalContext.current
                        val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
                        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                            contentContent()
                        }
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
                val context = LocalContext.current
                val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
                CompositionLocalProvider(
                    LocalComponentColors provides componentColors,
                    LocalNavigationState provides navigationState,
                    LocalAsyncImagePreviewHandler provides previewHandler,
                ) {
                    contentContent()
                }
            }
        }
    }
}
