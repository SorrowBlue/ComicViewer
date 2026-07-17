/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info.di

import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.bookshelfDeleteNavEntry
import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.bookshelfInfoNavEntry
import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.bookshelfNotificationNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookshelfInfoModule {
    @Provides
    @IntoSet
    private fun provideBookshelfInfoNavEntry(): ScreenEntryProvider = { navigator ->
        bookshelfInfoNavEntry(navigator, "Bookshelf")
    }

    @Provides
    @IntoSet
    private fun provideBookshelfNotificationNavEntry(): ScreenEntryProvider = { navigator ->
        bookshelfNotificationNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideBookshelfDeleteNavEntry(): ScreenEntryProvider = { navigator ->
        bookshelfDeleteNavEntry(navigator)
    }
}
