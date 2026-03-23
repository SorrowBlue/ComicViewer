package com.sorrowblue.comicviewer.feature.tutorial.di

import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavKey
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface TutorialModule {
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
