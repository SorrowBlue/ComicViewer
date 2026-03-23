package com.sorrowblue.comicviewer.feature.collection.editor.di

import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.basicCollectionCreateNavEntry
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.basicCollectionEditNavEntry
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.smartCollectionCreateNavEntry
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.smartCollectionEditNavEntry
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface CollectionEditModule {
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
