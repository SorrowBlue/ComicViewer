package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.calculateFromAdaptiveInfo2
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.InsetsConfig
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    ComicTheme {
        val context = LocalContext.current
        val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
        CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler, content)
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
fun DeviceTemplate(
    template: DeviceTemplate,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    EdgeToEdgeTemplate(
        cfg = InsetsConfig(
            navMode = NavigationMode.Gesture,
            cameraCutoutMode = CameraCutoutMode.Middle,
            statusBarMode = if (template.showSystemBar) InsetMode.Visible else InsetMode.Hidden,
            navigationBarMode = if (template.showSystemBar) InsetMode.Visible else InsetMode.Hidden,
            showInsetsBorder = false,
            isInvertedOrientation = template.isInvertedOrientation
        ),
        modifier = modifier
    ) {
        content()
    }
}

@PreviewMultiScreen
@Composable
private fun DeviceTemplatePreview(@PreviewParameter(DeviceTemplateProvider::class) template: DeviceTemplate) {
    PreviewTheme {
        DeviceTemplate(template) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing
            ) {
                ScratchBox(
                    Color.Blue,
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                )
            }
        }
    }
}

class DeviceTemplateProvider : PreviewParameterProvider<DeviceTemplate> {
    override val values: Sequence<DeviceTemplate>
        get() = sequenceOf(
            DeviceTemplate(true, false),
            DeviceTemplate(true, true),
        )
}

data class DeviceTemplate(
    val showSystemBar: Boolean = true,
    val isInvertedOrientation: Boolean = false,
)

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme2(
    modifier: Modifier = Modifier,
    showDeviceFrame: Boolean = true,
    showNavigation: Boolean = true,
    showStatusBar: Boolean = true,
    template: EdgeToEdgeTemplate = EdgeToEdgeTemplate(),
    content: @Composable () -> Unit,
) {
    val contentContent = remember { movableContentOf(content) }
    Box(modifier = modifier) {
        if (showDeviceFrame) {
            ComicTheme {
                EdgeToEdgeTemplate(
                    navMode = template.navMode,
                    cameraCutoutMode = template.cameraCutoutMode,
                    showInsetsBorder = template.showInsetsBorder,
                    statusBarMode = if (showStatusBar && template.isStatusBarVisible) InsetMode.Visible else InsetMode.Hidden,
                    navigationBarMode = if (template.isNavigationBarVisible) InsetMode.Visible else InsetMode.Hidden,
                    isInvertedOrientation = template.isInvertedOrientation,
                ) {
                    val navigationState = NavigationSuiteScaffoldDefaults
                        .calculateFromAdaptiveInfo2(currentWindowAdaptiveInfo(), showNavigation)
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
                val context = LocalContext.current
                val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
                CompositionLocalProvider(
                    LocalNavigationState provides navigationState,
                    LocalAsyncImagePreviewHandler provides previewHandler,
                ) {
                    contentContent()
                }
            }
        }
    }
}
