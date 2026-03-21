package com.sorrowblue.comicviewer.feature.tutorial.navigation

import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface TutorialProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(TutorialNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideTutorialNavEntry(
        factory: TutorialScreenContext.Factory,
    ): ScreenEntryProvider = {
        with(factory) {
            tutorialNavEntry(it)
        }
    }
}
