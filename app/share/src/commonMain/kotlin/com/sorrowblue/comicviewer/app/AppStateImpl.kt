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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    return remember(snackbarHostState) {
        AppStateImpl(
            navigationSuiteType = navigationSuiteType,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
        )
    }.apply {
        // Update navigationSuiteType when window size changes
        this.navigationSuiteType = navigationSuiteType
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
private class AppStateImpl(
    navigationSuiteType: NavigationSuiteType,
    override val snackbarHostState: SnackbarHostState,
    override val coroutineScope: CoroutineScope,
) : AppState {
    override var navigationSuiteType by mutableStateOf(navigationSuiteType)
}

internal val ProvidesAppState
    @Composable
    get() = LocalAppState provides rememberAppState()
