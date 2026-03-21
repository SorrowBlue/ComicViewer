package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface CollectionEditProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        BasicCollectionCreateNavKey.serializer().asEntry(),
        BasicCollectionEditNavKey.serializer().asEntry(),
        SmartCollectionEditNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideBasicCollectionCreateNavEntry(
        factory: BasicCollectionCreateScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            basicCollectionCreateNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBasicCollectionEditNavEntry(
        factory: BasicCollectionEditScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            basicCollectionEditNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionCreateNavEntry(
        factory: SmartCollectionCreateScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            smartCollectionCreateNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionEditNavEntry(
        factory: SmartCollectionEditScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            smartCollectionEditNavEntry(navigator)
        }
    }
}
