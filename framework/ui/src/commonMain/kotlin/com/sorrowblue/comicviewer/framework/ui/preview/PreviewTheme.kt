package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.annotation.ExperimentalCoilApi
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.InsetMode
import de.drick.compose.edgetoedgepreviewlib.NavigationMode
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewTheme(show: Boolean = false, content: @Composable () -> Unit) {
    val movableContent = remember {
        movableContentOf {
            SharedTransitionLayout {
                CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                    AnimatedContent(true) {
                        if (it) {
                            CompositionLocalProvider(
                                ProvidesAppState,
                                LocalNavAnimatedContentScope provides this,
                            ) {
                                ComicTheme {
                                    CompositionLocalProvider(
                                        provideAsyncImagePreviewHandler,
                                        content,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (show) {
        EdgeToEdgeTemplate(
            navMode = NavigationMode.Gesture,
            navigationBarMode = InsetMode.Visible,
            statusBarMode = InsetMode.Visible,
            cameraCutoutMode = CameraCutoutMode.Middle,
            showInsetsBorder = show,
            useHiddenApiHack = show,
        ) {
            movableContent()
        }
    } else {
        movableContent()
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
