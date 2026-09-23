/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
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

/**
 * Returns a metadata map containing [NavigationSuiteKey].
 */
fun navigationSuite(): Map<String, Any> = metadata {
    navigationSuite()
}

val LocalAdaptiveNavigationSuiteScaffoldState =
    staticCompositionLocalOf<AdaptiveNavigationSuiteScaffoldState?> { null }

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

/**
 * Provides content to the primary action slot (e.g. Floating Action Button) of the enclosing
 * [AdaptiveNavigationSuiteScaffold].
 */
@Composable
fun PrimaryActionContent(content: @Composable () -> Unit) {
    val slot = LocalPrimaryActionSlot.current ?: return
    DisposableEffect(content) {
        slot.value = content
        onDispose {
            slot.value = null
        }
    }
}

/**
 * Convenience wrapper for [PrimaryActionButton] using [LocalAdaptiveNavigationSuiteScaffoldState].
 */
@Composable
fun PrimaryActionButton(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val scaffoldState = LocalAdaptiveNavigationSuiteScaffoldState.current
    if (scaffoldState != null) {
        scaffoldState.PrimaryActionButton(
            onClick = onClick,
            text = text,
            icon = icon,
            modifier = modifier,
            visible = visible,
        )
    }
}

/**
 * Scope for [PrimaryActionButtonMenu] content providing [floatingActionButtonState].
 */
interface PrimaryActionButtonMenuScope : FloatingActionButtonMenuScope {
    val floatingActionButtonState: FloatingActionButtonState
}

private class PrimaryActionButtonMenuScopeImpl(
    private val scaffoldState: AdaptiveNavigationSuiteScaffoldState,
    menuScope: FloatingActionButtonMenuScope,
) : PrimaryActionButtonMenuScope,
    FloatingActionButtonMenuScope by menuScope {
    override val floatingActionButtonState: FloatingActionButtonState
        get() = scaffoldState.floatingActionButtonState
}

/**
 * Convenience wrapper for [PrimaryActionButtonMenu] using [LocalAdaptiveNavigationSuiteScaffoldState].
 */
@Composable
fun PrimaryActionButtonMenu(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable PrimaryActionButtonMenuScope.() -> Unit,
) {
    val scaffoldState = LocalAdaptiveNavigationSuiteScaffoldState.current
    if (scaffoldState != null) {
        with(scaffoldState) {
            PrimaryActionButtonMenu(
                modifier = modifier,
                visible = visible,
            ) {
                val menuScope = this
                val scope = remember(menuScope, scaffoldState) {
                    PrimaryActionButtonMenuScopeImpl(scaffoldState, menuScope)
                }
                with(scope) {
                    content()
                }
            }
        }
    }
}

/**
 * Returns the current [NavigationSuiteType], falling back to window calculation if not inside
 * an [AdaptiveNavigationSuiteScaffold].
 */
@Composable
fun currentNavigationSuiteType(): NavigationSuiteType =
    LocalAdaptiveNavigationSuiteScaffoldState.current?.navigationSuiteType
        ?: NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfoV2())

@ContributesIntoSet(AppScope::class)
internal class NavigationSuiteNavEntryDecorator : NavEntryDecoratorProvider {

    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> =
        rememberNavigationSuiteNavEntryDecorator()
}

@Composable
fun <T : Any> rememberNavigationSuiteNavEntryDecorator(): NavEntryDecorator<T> =
    remember { NavigationSuiteDecorator() }

private class NavigationSuiteDecorator<T : Any> :
    NavEntryDecorator<T>(
        decorate = { entry ->
            NavigationSuiteEntryContent(entry)
        },
        onPop = {},
    )

@Composable
private fun <T : Any> NavigationSuiteEntryContent(entry: NavEntry<T>) {
    if (entry.metadata[NavigationSuiteKey] != null) {
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
                entry.Content()
            }
        }
    } else {
        entry.Content()
    }
}
