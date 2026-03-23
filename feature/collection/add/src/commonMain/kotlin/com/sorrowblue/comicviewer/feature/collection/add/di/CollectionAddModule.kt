package com.sorrowblue.comicviewer.feature.collection.add.di

import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenContext
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.collection.add.navigation.basicCollectionAddNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface CollectionAddModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        BasicCollectionAddNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideBasicCollectionAddNavEntry(
        factory: BasicCollectionAddScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            basicCollectionAddNavEntry(navigator)
        }
    }
}
