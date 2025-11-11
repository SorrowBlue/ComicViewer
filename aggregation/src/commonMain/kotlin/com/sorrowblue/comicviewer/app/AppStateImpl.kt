package com.sorrowblue.comicviewer.app

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import kotlinx.coroutines.CoroutineScope

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
        AppStateImpl(navigationSuiteType, sharedTransitionScope, snackbarHostState)
    }
    appState.navigationSuiteType = navigationSuiteType
    appState.snackbarHostState = snackbarHostState
    appState.coroutineScope = rememberCoroutineScope()
    return appState
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class AppStateImpl(
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
