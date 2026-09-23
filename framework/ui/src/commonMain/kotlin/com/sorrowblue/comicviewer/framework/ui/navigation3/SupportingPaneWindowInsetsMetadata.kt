/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass

class SupportingPaneWindowInsetsMetadata<T : NavKey>(val clazz: KClass<T>)

const val SupportingPaneWindowInsetsKey =
    "com.sorrowblue.comicviewer.framework.ui.navigation3.SupportingPaneWindowInsets"

inline fun <reified T : NavKey> SupportingPaneSceneStrategy.Companion.mainPane(sceneKey: String) =
    SupportingPaneSceneStrategy.mainPane(sceneKey) +
        mapOf(SupportingPaneWindowInsetsKey to SupportingPaneWindowInsetsMetadata(T::class))


@ContributesIntoSet(AppScope::class)
internal class SupportingPaneWindowInsets : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> {
        val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
        return rememberSupportingPaneWindowInsetsDecorator(directive = directive)
    }
}

@Composable
internal fun <T : Any> rememberSupportingPaneWindowInsetsDecorator(
    directive: PaneScaffoldDirective,
): NavEntryDecorator<T> = remember(directive) { SupportingPaneWindowInsetsDecorator(directive) }

private class SupportingPaneWindowInsetsDecorator<T : Any>(directive: PaneScaffoldDirective) :
    NavEntryDecorator<T>(
        decorate = { entry ->
            if (entry.metadata.contains(
                    SupportingPaneWindowInsetsKey,
                ) && directive.maxHorizontalPartitions != 1
            ) {
                // Two pane
                val currentStackClass = LocalNavigator.current.backStack.last()::class
                val currentPaneClass = entry.metadata.getValue(SupportingPaneWindowInsetsKey)
                    .cast<SupportingPaneWindowInsetsMetadata<*>>().clazz
                val insets = if (currentStackClass == currentPaneClass) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.End)
                } else {
                    WindowInsets()
                }
                Box(modifier = Modifier.consumeWindowInsets(insets)) {
                    entry.Content()
                }
            } else {
                entry.Content()
            }
        },
        onPop = {},
    )

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T : Any> Any.cast(): T = this as T
