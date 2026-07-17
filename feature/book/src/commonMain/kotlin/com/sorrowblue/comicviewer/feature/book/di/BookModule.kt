/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.di

import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.book.navigation.bookMenuNavEntry
import com.sorrowblue.comicviewer.feature.book.navigation.bookNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BookModule {
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
