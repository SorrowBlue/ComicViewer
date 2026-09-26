/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Registers an action to be invoked when the current navigation tab is re-selected.
 */
@Composable
fun NavigationReSelectEffect(onReSelect: () -> Unit) {
    val registry = LocalNavigationReSelectRegistry.current ?: return
    val currentOnReSelect by rememberUpdatedState(onReSelect)
    DisposableEffect(registry, currentOnReSelect) {
        registry.register(currentOnReSelect)
        onDispose {
            registry.unregister(currentOnReSelect)
        }
    }
}
