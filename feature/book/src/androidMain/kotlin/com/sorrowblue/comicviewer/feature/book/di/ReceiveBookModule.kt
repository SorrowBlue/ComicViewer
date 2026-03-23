package com.sorrowblue.comicviewer.feature.book.di

import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookNavKey
import com.sorrowblue.comicviewer.feature.book.navigation.receiveBookNavEntry
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface ReceiveBookModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(ReceiveBookNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideReceiveBookNavEntry(
        factory: ReceiveBookScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            receiveBookNavEntry(navigator)
        }
    }
}
