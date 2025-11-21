package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope

val LocalAppState = staticCompositionLocalOf<AppState> {
    error("No AppState provided")
}

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    error("No SharedTransitionScope provided")
}

@OptIn(ExperimentalSharedTransitionApi::class)
interface AppState : SharedTransitionScope {
    var navigationSuiteType: NavigationSuiteType
    val snackbarHostState: SnackbarHostState
    val coroutineScope: CoroutineScope
}
