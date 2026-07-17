/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater.di

import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterFolderFileInfoNavEntry
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterNavEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ReadLaterModule {
    @Provides
    @IntoSet
    private fun provideReadLaterNavEntry(
        factory: ReadLaterScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            readLaterNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        readLaterFileInfoNavEntry(navigator)
    }

    @Provides
    @IntoSet
    private fun provideReadLaterFolderFileInfoNavEntry(): ScreenEntryProvider = { navigator ->
        readLaterFolderFileInfoNavEntry(navigator)
    }
}
