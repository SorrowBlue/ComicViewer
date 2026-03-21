package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookshelfInfoProviders {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        BookshelfInfoNavKey.serializer().asEntry(),
        BookshelfDeleteNavKey.serializer().asEntry(),
        BookshelfNotificationNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideBookshelfInfoNavEntry(
        factory: BookshelfInfoScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            bookshelfInfoNavEntry(navigator, "Bookshelf")
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfNotificationNavEntry(): ScreenEntryProvider = { navigator ->
        bookshelfNotificationNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideBookshelfDeleteNavEntry(
        factory: BookshelfDeleteScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            bookshelfDeleteNavEntry(navigator)
        }
    }
}
