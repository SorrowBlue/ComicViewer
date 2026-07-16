/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.serialization.Serializable

@Serializable
data object TutorialNavKey : NavKey

@ContributesIntoSet(AppScope::class)
internal class TutorialNavEntry : NavigationEntryProvider {

    context(scope: EntryProviderScope<NavKey>)
    override fun invoke(navigator: Navigator) {
        tutorialNavEntry(navigator)
    }
}

context(scope: EntryProviderScope<NavKey>)
private fun tutorialNavEntry(navigator: Navigator) {
    scope.entry<TutorialNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        TutorialScreenRoot(onComplete = navigator::goBack)
    }
}
