package com.sorrowblue.comicviewer.feature.book.di

import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.book.navigation.BookMenuNavKey
import com.sorrowblue.comicviewer.feature.book.navigation.bookMenuNavEntry
import com.sorrowblue.comicviewer.feature.book.navigation.bookNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation.NavScope
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(NavScope::class)
interface BookModule {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(BookMenuNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideBookNavEntry(factory: BookScreenContext.Factory): ScreenEntryProvider =
        { navigator ->
            with(factory) {
                bookNavEntry(navigator)
            }
        }

    @Provides
    @IntoSet
    private fun provideBookMenuNavEntry(
        factory: BookMenuScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            bookMenuNavEntry(navigator)
        }
    }
}
