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
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

private const val SupportingPaneRoleKey: String =
    "androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole"

private val MainMetadataClass =
    requireNotNull(SupportingPaneSceneStrategy.mainPane()[SupportingPaneRoleKey])::class

private val ExtraMetadataClass =
    requireNotNull(SupportingPaneSceneStrategy.extraPane()[SupportingPaneRoleKey])::class

private val SupportingMetadataClass =
    requireNotNull(SupportingPaneSceneStrategy.supportingPane()[SupportingPaneRoleKey])::class

private const val ListDetailRoleKey: String =
    "androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole"

private val ListMetadataClass =
    requireNotNull(ListDetailSceneStrategy.listPane()[ListDetailRoleKey])::class

private val DetailMetadataClass =
    requireNotNull(ListDetailSceneStrategy.detailPane()[ListDetailRoleKey])::class

private val NavEntry<*>.isSupportingMainPane: Boolean
    get() = metadata[SupportingPaneRoleKey]?.let { it::class } == MainMetadataClass

internal val NavEntry<*>.isSupportingExtraOrSupportingPane: Boolean
    get() {
        val clazz = metadata[SupportingPaneRoleKey]?.let { it::class }
        return clazz == ExtraMetadataClass || clazz == SupportingMetadataClass
    }

private val NavEntry<*>.isListPane: Boolean
    get() = metadata[ListDetailRoleKey]?.let { it::class } == ListMetadataClass

internal val NavEntry<*>.isDetailPane: Boolean
    get() = metadata[ListDetailRoleKey]?.let { it::class } == DetailMetadataClass

private fun <T : Any> NavEntry<T>.withConsumedInsets(sides: WindowInsetsSides): NavEntry<T> =
    NavEntry(navEntry = this) {
        Box(
            modifier = Modifier.consumeWindowInsets(
                WindowInsets.safeDrawing.only(sides),
            ),
        ) {
            this@withConsumedInsets.Content()
        }
    }

fun <T : Any> SupportingPaneSceneStrategy<T>.insetsAware(
    directive: PaneScaffoldDirective,
): SceneStrategy<T> = InsetsAwareSupportingPaneSceneStrategy(this, directive)

fun <T : Any> ListDetailSceneStrategy<T>.insetsAware(
    directive: PaneScaffoldDirective,
): SceneStrategy<T> = InsetsAwareListDetailSceneStrategy(this, directive)

private class InsetsAwareSupportingPaneSceneStrategy<T : Any>(
    private val delegate: SupportingPaneSceneStrategy<T>,
    private val directive: PaneScaffoldDirective,
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (directive.maxHorizontalPartitions <= 1) {
            return with(delegate) { calculateScene(entries) }
        }

        val lastEntry = entries.lastOrNull()
        val isExtraPaneActive = lastEntry?.isSupportingExtraOrSupportingPane == true

        val modifiedEntries = entries.map { entry ->
            when {
                entry.isSupportingMainPane && isExtraPaneActive -> {
                    entry.withConsumedInsets(WindowInsetsSides.End)
                }

                entry.isSupportingExtraOrSupportingPane -> {
                    entry.withConsumedInsets(WindowInsetsSides.Start)
                }

                else -> entry
            }
        }

        return with(delegate) { calculateScene(modifiedEntries) }
    }
}

private class InsetsAwareListDetailSceneStrategy<T : Any>(
    private val delegate: ListDetailSceneStrategy<T>,
    private val directive: PaneScaffoldDirective,
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (directive.maxHorizontalPartitions <= 1) {
            return with(delegate) { calculateScene(entries) }
        }

        val modifiedEntries = entries.map { entry ->
            when {
                entry.isListPane -> {
                    entry.withConsumedInsets(WindowInsetsSides.End)
                }

                entry.isDetailPane -> {
                    entry.withConsumedInsets(WindowInsetsSides.Start)
                }

                else -> entry
            }
        }

        return with(delegate) { calculateScene(modifiedEntries) }
    }
}
