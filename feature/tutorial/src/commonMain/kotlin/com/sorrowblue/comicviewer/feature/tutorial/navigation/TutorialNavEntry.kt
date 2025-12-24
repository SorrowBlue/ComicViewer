package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: TutorialScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.tutorialNavEntry(navigator: Navigator) {
    entry<TutorialNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        with(rememberRetained { factory.createTutorialScreenContext() }) {
            TutorialScreenRoot(onComplete = navigator::goBack)
        }
    }
}
