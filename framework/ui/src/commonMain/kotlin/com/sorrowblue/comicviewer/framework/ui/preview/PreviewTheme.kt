package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.annotation.ExperimentalCoilApi
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalNavigationItems
import com.sorrowblue.comicviewer.framework.ui.adaptive.NavigationItems
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme(
    modifier: Modifier = Modifier,
    showDeviceUi: Boolean = false,
    showInsetsBorder: Boolean = false,
    content: @Composable () -> Unit,
) {
    val movableContent = remember {
        movableContentOf {
            SharedTransitionLayout {
                CompositionLocalProvider(
                    ProvidesAppState,
                    provideAsyncImagePreviewHandler,
                    ProvidesPreviewNavigationItems,
                    LocalSharedTransitionScope provides this,
                ) {
                    AnimatedContent(true, modifier = modifier) {
                        if (it) {
                            CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                                ComicTheme {
                                    content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDeviceUi) {
        EdgeToEdgeTemplate(
            navMode = NavigationMode.Gesture,
            navigationBarMode = InsetMode.Visible,
            statusBarMode = InsetMode.Visible,
            cameraCutoutMode = CameraCutoutMode.Middle,
            showInsetsBorder = showInsetsBorder,
            useHiddenApiHack = false,
        ) {
            movableContent()
        }
    } else {
        movableContent()
    }
}

private val ProvidesPreviewNavigationItems
    @Composable
    get() = LocalNavigationItems provides PreviewNavigationItems

private val PreviewNavigationItems = object : NavigationItems {
    @Composable
    override fun Content(onNavigationReSelect: () -> Unit) {
        repeat(4) {
            NavigationSuiteItem(
                selected = true,
                onClick = {},
                icon = {
                    Icon(ComicIcons.Favorite, null)
                },
                label = {
                    Text("label")
                },
            )
        }
    }
}

context(scope: SharedTransitionScope)
internal val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberAppState(scope)

@OptIn(ExperimentalSharedTransitionApi::class)
private class PreviewAppState(
    navigationSuiteType: NavigationSuiteType,
    sharedTransitionScope: SharedTransitionScope,
    override var snackbarHostState: SnackbarHostState,
) : AppState,
    SharedTransitionScope by sharedTransitionScope {
    override var navigationSuiteType by mutableStateOf(navigationSuiteType)
    override lateinit var coroutineScope: CoroutineScope
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun rememberAppState(
    sharedTransitionScope: SharedTransitionScope,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): AppState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    val appState = remember {
        PreviewAppState(navigationSuiteType, sharedTransitionScope, snackbarHostState)
    }
    appState.navigationSuiteType = navigationSuiteType
    appState.snackbarHostState = snackbarHostState
    appState.coroutineScope = rememberCoroutineScope()
    return appState
}
