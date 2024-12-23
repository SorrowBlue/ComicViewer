package com.sorrowblue.comicviewer.framework.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CompliantNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
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

data class PreviewConfig(
    val isInvertedOrientation: Boolean = false,
    val cutout: Boolean = true,
    val systemBar: Boolean = true,
    val navigation: Boolean = true,
)

@Composable
fun PreviewDevice(
    config: PreviewConfig = PreviewConfig(),
    content: @Composable () -> Unit,
) {
    PreviewTheme {
        EdgeToEdgeTemplate(
            cfg = InsetsConfig.GestureNav.copy(
                isInvertedOrientation = config.isInvertedOrientation,
                cameraCutoutMode = if (config.cutout) CameraCutoutMode.Middle else CameraCutoutMode.None,
                statusBarMode = if (config.systemBar) InsetMode.Visible else InsetMode.Hidden,
                navigationBarMode = if (config.systemBar) InsetMode.Visible else InsetMode.Hidden
            ),
            isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE,
        ) {
            content()
        }
    }
}

@Composable
fun PreviewCompliantNavigation(
    modifier: Modifier = Modifier,
    config: PreviewConfig = PreviewConfig(),
    content: @Composable () -> Unit,
) {
    PreviewDevice(config = config) {
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
            navigationState = if (config.navigation) {
                navigationState
            } else {
                when (navigationState) {
                    is NavigationState.NavigationBar -> navigationState.copy(false)
                    is NavigationState.NavigationRail -> navigationState.copy(false)
                }
            },
            modifier = modifier
        ) {
            content()
        }
    }
}

@Composable
fun <T : Any> PreviewCanonicalScaffold(
    modifier: Modifier = Modifier,
    config: PreviewConfig = PreviewConfig(),
    navigator: ThreePaneScaffoldNavigator<T> = rememberSupportingPaneScaffoldNavigator<T>(),
    topBar: @Composable (() -> Unit)? = null,
    extraPane: @Composable ((T) -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    PreviewCompliantNavigation(config = config) {
        CanonicalScaffold(
            navigator = navigator,
            topBar = topBar,
            floatingActionButton = floatingActionButton,
            extraPane = extraPane,
            modifier = modifier,
            content = content
        )
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewDevicePreview(@PreviewParameter(PreviewConfigProvider::class) config: PreviewConfig) {
    PreviewDevice(config = config) {}
}

private class PreviewConfigProvider : PreviewParameterProvider<PreviewConfig> {
    override val values
        get() = sequenceOf(
            PreviewConfig(isInvertedOrientation = false, cutout = true, systemBar = true),
            PreviewConfig(isInvertedOrientation = true, cutout = true, systemBar = true),
            PreviewConfig(isInvertedOrientation = false, cutout = true, systemBar = false),
            PreviewConfig(isInvertedOrientation = true, cutout = true, systemBar = false),
            PreviewConfig(isInvertedOrientation = false, cutout = false, systemBar = true),
            PreviewConfig(isInvertedOrientation = false, cutout = false, systemBar = false),
        )
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
