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
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass

class SupportingPaneWindowInsetsMetadata<T : NavKey>(val clazz: KClass<T>)

data object SupportingPaneWindowInsetsKey : NavMetadataKey<SupportingPaneWindowInsetsMetadata<*>>

inline fun <reified T : NavKey> SupportingPaneSceneStrategy.Companion.mainPaneV2(sceneKey: String) =
    SupportingPaneSceneStrategy.mainPane(sceneKey) + metadata {
        put(SupportingPaneWindowInsetsKey, SupportingPaneWindowInsetsMetadata(T::class))
    }

@ContributesIntoSet(AppScope::class)
internal class SupportingPaneWindowInsets : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> {
        val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
        return remember(directive) { SupportingPaneWindowInsetsDecorator(directive) }
    }
}

private class SupportingPaneWindowInsetsDecorator<T : Any>(directive: PaneScaffoldDirective) :
    NavEntryDecorator<T>(
        decorate = { entry ->
            val metadata = entry.metadata[SupportingPaneWindowInsetsKey]
            if (metadata != null && directive.maxHorizontalPartitions != 1) {
                // Two pane
                val currentStackClass = LocalNavigator.current.backStack.last()::class
                val currentPaneClass = metadata.clazz
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
