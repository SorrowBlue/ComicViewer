/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.Transitions
import com.sorrowblue.comicviewer.framework.ui.navigation3.insetsAware
import logcat.logcat

@Composable
context(context: PlatformContext)
internal fun ComicViewerNavDisplay(
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    modifier: Modifier = Modifier,
    navEntryDecorators: @Composable () -> List<NavEntryDecorator<NavKey>> =
        rememberNavEntryDecorators(),
) {
    val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val supportingPaneSceneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
        directive = directive,
    )
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
        directive = directive,
    )
    val dialogSceneStrategy = remember { DialogSceneStrategy<NavKey>() }
    val sceneStrategies = remember(
        supportingPaneSceneStrategy,
        listDetailSceneStrategy,
        dialogSceneStrategy,
        directive,
    ) {
        listOf(
            supportingPaneSceneStrategy.insetsAware(directive),
            listDetailSceneStrategy.insetsAware(directive),
            dialogSceneStrategy,
        )
    }
    Transitions.InitSlideDistance()
    Transitions.motionScheme = ComicTheme.motionScheme
    NavDisplay(
        modifier = modifier,
        entries = navigator.state.toDecoratedEntries(
            entryDecorators = navEntryDecorators(),
            entryProvider = entryProvider,
        ),
        sceneStrategies = sceneStrategies,
        onBack = {
            logcat("Navigator") { "ComicViewerUI: #onBack" }
            navigator.goBack()
        },
    )
}
