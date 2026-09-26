/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet

/**
 * Metadata key indicating that a navigation entry should be wrapped in an
 * [AdaptiveNavigationSuiteScaffold].
 */
data object NavigationSuiteKey : NavMetadataKey<Unit>

/**
 * Adds navigation suite scaffolding to this navigation entry within a [MetadataScope].
 */
fun MetadataScope.navigationSuite() = put(NavigationSuiteKey, Unit)

val LocalPrimaryActionSlot =
    staticCompositionLocalOf<MutableState<(@Composable () -> Unit)?>?> { null }

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

val LocalNavigationReSelectRegistry =
    compositionLocalOf<NavigationReSelectRegistry?> { null }

@ContributesIntoSet(AppScope::class)
internal class AdaptiveNavigationSuiteDecoratorProvider : NavEntryDecoratorProvider {

    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> {
        return remember {
            NavEntryDecorator(
                decorate = { entry ->
                    if (entry.metadata[NavigationSuiteKey] != null) {
                        NavigationSuiteEntryContent(entry::Content)
                    } else {
                        entry.Content()
                    }
                },
                onPop = {}
            )
        }
    }
}

@Composable
private fun NavigationSuiteEntryContent(content: @Composable () -> Unit) {
    val registry = remember { NavigationReSelectRegistry() }
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState(
        onNavigationReSelect = registry::onReSelect,
    )
    val primaryActionSlot = remember {
        mutableStateOf<(@Composable () -> Unit)?>(null)
    }
    CompositionLocalProvider(
        LocalAdaptiveNavigationSuiteScaffoldState provides scaffoldState,
        LocalPrimaryActionSlot provides primaryActionSlot,
        LocalNavigationReSelectRegistry provides registry,
    ) {
        scaffoldState.AdaptiveNavigationSuiteScaffold(
            primaryActionContent = {
                primaryActionSlot.value?.invoke()
            },
        ) {
            content()
        }
    }
}
