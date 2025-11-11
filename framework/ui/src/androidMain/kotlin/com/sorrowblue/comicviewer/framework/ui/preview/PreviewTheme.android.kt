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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.preview.fake.PreviewImage
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalCoilApi::class)
@Composable
actual fun PreviewTheme(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        AnimatedContent(true) {
            if (it) {
                CompositionLocalProvider(
                    ProvidesAppState,
                    LocalNavAnimatedContentScope provides this,
                ) {
                    ComicTheme {
                        val context = LocalContext.current
                        val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
                        CompositionLocalProvider(
                            LocalAsyncImagePreviewHandler provides previewHandler,
                            content,
                        )
                    }
                }
            }
        }
    }
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

context(scope: SharedTransitionScope)
internal val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberAppState(scope)
