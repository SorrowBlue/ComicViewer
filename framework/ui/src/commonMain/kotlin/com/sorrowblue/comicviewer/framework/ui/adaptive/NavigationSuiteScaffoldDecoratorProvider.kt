/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation3.runtime.MetadataScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import com.sorrowblue.comicviewer.framework.ui.navigation3.isDetailPane
import com.sorrowblue.comicviewer.framework.ui.navigation3.isSupportingExtraOrSupportingPane
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.launch

data object NavigationSuiteKey : NavMetadataKey<Boolean>

fun MetadataScope.navigationSuite(show: Boolean = true) = put(NavigationSuiteKey, show)

val LocalNavigationSuiteScaffoldState = compositionLocalOf<NavigationSuiteScaffoldState?> { null }

const val SupportingPaneRoleKey: String =
    "androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole"

private val NavEntry<*>.isSubPane: Boolean
    get() = isSupportingExtraOrSupportingPane || isDetailPane

private val NavEntry<*>.isDialog: Boolean
    get() = metadata.contains(DialogSceneStrategy.Companion.DialogKey)

@ContributesIntoSet(AppScope::class)
internal class NavigationSuiteDecoratorProvider : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> {
        val state = LocalNavigationSuiteScaffoldState.current
        val coroutineScope = rememberCoroutineScope()
        val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
        val isMultiPane = directive.maxHorizontalPartitions > 1
        return remember(state, isMultiPane, coroutineScope) {
            NavEntryDecorator(
                decorate = { entry ->
                    val isDialog = entry.isDialog
                    val isSubPane = entry.isSubPane
                    val shouldSkip = isDialog || (isMultiPane && isSubPane)
                    val isNavigationSuite = entry.metadata[NavigationSuiteKey] ?: false
                    if (!shouldSkip) {
                        LifecycleResumeEffect(isNavigationSuite) {
                            val job = coroutineScope.launch {
                                if (isNavigationSuite) {
                                    state?.show()
                                } else {
                                    state?.hide()
                                }
                            }
                            onPauseOrDispose {
                                job.cancel()
                            }
                        }
                    }
                    entry.Content()
                },
                onPop = {},
            )
        }
    }
}
