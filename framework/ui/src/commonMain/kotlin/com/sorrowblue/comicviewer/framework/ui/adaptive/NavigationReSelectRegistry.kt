/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

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

fun provideNavigationReSelectRegistry(registry: NavigationReSelectRegistry): ProvidedValue<*> =
    LocalNavigationReSelectRegistry provides registry

class NavigationReSelectRegistry {

    private var callback: (() -> Unit)? = null

    fun register(callback: () -> Unit) {
        this.callback = callback
    }

    fun unregister(callback: () -> Unit) {
        if (this.callback == callback) {
            this.callback = null
        }
    }

    fun onReSelect() {
        callback?.invoke()
    }
}

private val LocalNavigationReSelectRegistry =
    compositionLocalOf<NavigationReSelectRegistry?> { null }
